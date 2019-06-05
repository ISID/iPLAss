/*
 * Copyright (C) 2019 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
 *
 * Unless you have purchased a commercial license,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.iplass.gem.command.generic.bulk;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.iplass.gem.GemConfigService;
import org.iplass.gem.command.CommandUtil;
import org.iplass.gem.command.Constants;
import org.iplass.gem.command.generic.detail.NestTableReferenceRegistHandler;
import org.iplass.gem.command.generic.detail.ReferenceRegistHandler;
import org.iplass.gem.command.generic.detail.RegistrationCommandContext;
import org.iplass.gem.command.generic.detail.RegistrationPropertyBaseHandler;
import org.iplass.mtp.ApplicationException;
import org.iplass.mtp.command.RequestContext;
import org.iplass.mtp.entity.Entity;
import org.iplass.mtp.entity.EntityManager;
import org.iplass.mtp.entity.LoadOption;
import org.iplass.mtp.entity.ValidateError;
import org.iplass.mtp.entity.definition.EntityDefinition;
import org.iplass.mtp.entity.definition.EntityDefinitionManager;
import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.entity.definition.properties.ReferenceProperty;
import org.iplass.mtp.impl.util.ConvertUtil;
import org.iplass.mtp.spi.ServiceRegistry;
import org.iplass.mtp.util.StringUtil;
import org.iplass.mtp.view.generic.BulkFormView;
import org.iplass.mtp.view.generic.EntityViewUtil;
import org.iplass.mtp.view.generic.FormViewUtil;
import org.iplass.mtp.view.generic.editor.DateRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.JoinPropertyEditor;
import org.iplass.mtp.view.generic.editor.NestProperty;
import org.iplass.mtp.view.generic.editor.PropertyEditor;
import org.iplass.mtp.view.generic.editor.ReferencePropertyEditor;
import org.iplass.mtp.view.generic.editor.ReferencePropertyEditor.ReferenceDisplayType;
import org.iplass.mtp.view.generic.element.Element;
import org.iplass.mtp.view.generic.element.property.PropertyItem;
import org.iplass.mtp.view.generic.element.section.DefaultSection;
import org.iplass.mtp.view.generic.element.section.Section;
import org.iplass.mtp.web.template.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiBulkCommandContext extends RegistrationCommandContext {

	private static Logger logger = LoggerFactory.getLogger(MultiBulkCommandContext.class);

	/** 一括画面用のFormレイアウト情報 */
	private BulkFormView view;

	private GemConfigService gemConfig = null;

	private List<BulkCommandParams> bulkCommandParams = new ArrayList<>();

	/** パラメータパターン */
	private Pattern pattern = Pattern.compile("^(\\d+)\\_(.+)?$");

	/**
	 * クライアントから配列で受け取ったパラメータは自動設定する対象外
	 */
	@SuppressWarnings("serial")
	private Set<String> skipProps = new HashSet<String>() {
		{
			add(Entity.OID);
			add(Entity.VERSION);
			add(Entity.UPDATE_DATE);
		}
	};

	@Override
	protected Logger getLogger() {
		return logger;
	}

	public MultiBulkCommandContext(RequestContext request, EntityManager entityLoader, EntityDefinitionManager definitionLoader) {
		super(request, entityLoader, definitionLoader);

		gemConfig = ServiceRegistry.getRegistry().getService(GemConfigService.class);
		init();
	}

	private void init() {
		populateBulkCommandParam(Constants.OID, String.class, true, true, false);
		populateBulkCommandParam(Constants.VERSION, Long.class, false, true, false);
		populateBulkCommandParam(Constants.TIMESTAMP, Long.class, false, false, true);
	}

	private void populateBulkCommandParam(String name, Class<?> cls, boolean create, boolean notBlank, boolean checkDiff) {
		//BulkUpdateAllCommandからのChainの可能性があるので、Attributeから取得する
		String[] param = (String[]) request.getAttribute(name);
		if (param == null || param.length == 0) {
			param = getParams(name);
		}
		if (param != null) {
			for (int i = 0; i < param.length; i++) {
				// 先頭に「行番号_」が付加されているので分離する
				String[] params = splitRowParam(param[i]);
				Integer targetRow = Integer.parseInt(params[0]);
				String targetParam = params[1];
				if (StringUtil.isBlank(targetParam) && notBlank) {
					getLogger().error("can not be empty. name=" + name + ", param=" + param[i]);
					throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.invalidFormat"));
				}
				BulkCommandParams bulkParams = getBulkCommandParams(targetRow);
				if (create) {
					// targetParamをキーとして設定する
					if (bulkParams != null) {
						getLogger().error("duplicate row. row=" + targetRow);
						throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.duplicateRow"));
					}
					bulkCommandParams.add(new BulkCommandParams(targetRow, targetParam));
				} else {
					if (bulkParams == null) {
						getLogger().error("selected row does not exist. params=" + Arrays.toString(params));
						throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.invalidRow"));
					}
					bulkParams.setValue(name, ConvertUtil.convertFromString(cls, targetParam));
				}

				if (checkDiff && i == param.length - 1) {
					// バージョン管理されているエンティティでマルチリファレンスのプロパティ定義がある場合、同じOIDとバージョンで行番号が異なるデータが存在するので、
					// 設定されたプロパティ値が同じ値であるかチェックします。
					boolean hasDiffPropValue = hasDifferentPropertyValue(name);
					if (hasDiffPropValue) {
						getLogger().error("has different prop value. name=" + name + ", bulkCommandParams=" + bulkCommandParams.toString());
						throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.diffPropVal"));
					}
				}
			}
		}
	}

	private String[] splitRowParam(String rowParam) {
		Matcher m = pattern.matcher(rowParam);
		if (!m.matches()) {
			getLogger().error("invalid parameter format. rowParam=" + rowParam);
			throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.invalidFormat"));
		}
		String[] params = new String[] { m.group(1), m.group(2) };
		return params;
	}

	private BulkCommandParams getBulkCommandParams(Integer row) {
		List<BulkCommandParams> paramsList = bulkCommandParams.stream()
				.filter(p -> p.getRow().equals(row))
				.collect(Collectors.toList());
		if (paramsList.size() == 0) {
			return null;
		} else if (paramsList.size() > 1) {
			getLogger().error("duplicate row. paramsList=" + paramsList);
			throw new ApplicationException(resourceString("command.generic.bulk.BulkCommandContext.duplicateRow"));
		}
		return paramsList.get(0);
	}

	private boolean hasDifferentPropertyValue(String propName) {
		Set<String> oids = getOids();
		for (String oid : oids) {
			for (Long version: getVersions(oid)) {
				List<Object> propValues = bulkCommandParams.stream()
						.filter(p -> p.getOid().equals(oid) && p.getVersion().equals(version))
						.map(p -> p.getValue(propName))
						.collect(Collectors.toList());
				Object first = propValues.get(0);
				if (first == null) {
					return propValues.stream().anyMatch(v -> v != null);
				} else {
					return propValues.stream().anyMatch(v -> !first.equals(v));
				}
			}
		}
		return false;
	}

	/**
	 * リクエストパラメータから参照型の更新データを作成します。
	 * @param p プロパティ定義
	 * @param prefix 参照型のプロパティのリクエストパラメータに設定されているプレフィックス
	 * @return プロパティの値
	 */
	@Override
	protected Object createReference(PropertyDefinition p, String prefix) {
		final ReferenceProperty rp = (ReferenceProperty) p;
		final String defName = rp.getObjectDefinitionName();

		//NestTableの場合の件数取得
		//prefixが付くケース=NestTable内の多重参照なのであり得ない
		//→件数取れないため通常の参照扱いで処理が終わる
		Long count = getLongValue(prefix + p.getName() + "_count");
		if (p.getMultiplicity() == 1) {
			Entity entity = null;
			if (count == null) {
				String key = getParam(prefix + p.getName());
				entity = getRefEntity(rp.getObjectDefinitionName(), key);
			} else {
				List<Entity> list = getRefTableValues(rp, defName, count, prefix);
				if (list.size() > 0) entity = list.get(0);
			}
			return entity;
		} else {
			List<Entity> list = null;
			if (count == null) {
				String[] params = getParams(prefix + p.getName());
				if (params != null) {
					list = Arrays.stream(params)
							.map(key -> getRefEntity(rp.getObjectDefinitionName(), key))
							.filter(value -> value != null)
							.collect(Collectors.toList());
				}
			} else {
				//参照型で参照先のデータを作成・編集するケース
				list = getRefTableValues(rp, defName, count, prefix);
			}

			if (list != null && !list.isEmpty()) {
				//マッピングクラスの配列を生成する
				EntityDefinition ed = getEntityDefinition();
				setEntityDefinition(definitionManager.get(defName));
				Entity emptyEntity = newEntity();
				setEntityDefinition(ed);

				Object[] array = (Object[]) Array.newInstance(emptyEntity.getClass(), list.size());
				return list.toArray(array);

			} else {
				return null;
			}
		}
	}

	private Entity getRefEntity(String definitionName, String key) {
		Entity entity = null;
		String oid = null;
		Long version = null;
		if (key != null) {
			int lastIndex = key.lastIndexOf("_");

			if (lastIndex < 0) {
				oid = key;
			} else {
				oid = key.substring(0, lastIndex);
				version = CommandUtil.getLong(key.substring(lastIndex + 1));
			}
		}
		if (StringUtil.isNotBlank(oid)) {
			//バリデーションエラー時に消えないようにデータ読み込み
			//gemの設定により、参照を合わせて読み込むか切り替える
			if (gemConfig.isLoadWithReference()) {
				entity = entityManager.load(oid, version, definitionName);
			} else {
				entity = entityManager.load(oid, version, definitionName, new LoadOption(false, false));
			}
		}
		return entity;
	}

	/**
	 * リクエストパラメータからテーブルの参照型データの値を取得します。
	 * @param p プロパティ定義
	 * @param defName 参照型のEntity定義名
	 * @param count 参照データの最大件数
	 * @return 参照データのリスト
	 */
	private List<Entity> getRefTableValues(ReferenceProperty p, String defName, Long count, String prefix) {
		final List<Entity> list = new ArrayList<Entity>();
		EntityDefinition ed = getEntityDefinition();
		EntityDefinition red = definitionManager.get(defName);
		setEntityDefinition(red);//参照先の定義に詰め替える
		for (int i = 0; i < count; i++) {
			//データあり
			String paramPrefix = prefix + p.getName() + "[" + Integer.toString(i) + "].";
			String errorPrefix = (i != list.size() ? prefix + p.getName() + "[" + Integer.toString(list.size()) + "]." : null);
			Entity entity = createEntityInternal(paramPrefix, errorPrefix);

			//入力エラー時に再Loadされないようにフラグ設定
			entity.setValue(Constants.REF_RELOAD, Boolean.FALSE);

			//Validationエラーが出るとhiddenに"null"が入るのでクリアする
			if (entity.getOid() != null && entity.getOid().equals("null")) {
				entity.setOid(null);
			}

			//Entity生成時にエラーが発生していないかチェック
			String checkPrefix = (errorPrefix != null ? errorPrefix : paramPrefix);
			boolean hasError = getErrors().stream()
					.filter(error -> error.getPropertyName().startsWith(checkPrefix))
					.findFirst().isPresent();

			//エラーがなくて、何もデータが入ってないものは破棄する
			if (hasError || !isEmpty(entity)) {
				entity.setDefinitionName(defName);
				entity.setValue(Constants.REF_INDEX, list.size());

				Long orderIndex = getLongValue("tableOrderIndex[" + i + "]");
				if (orderIndex != null) {
					entity.setValue(Constants.REF_TABLE_ORDER_INDEX, orderIndex);
				}

				list.add(entity);
			}
		}
		setEntityDefinition(ed);//元の定義に詰め替える

		// ネストテーブル用の登録処理を追加
		Optional<PropertyItem> ret = getProperty().stream().filter(pc -> pc.getPropertyName().equals(p.getName())).findFirst();
		if (ret.isPresent()) {
			addNestTableRegistHandler(p, list, red, ret.get());
		}

		return list;
	}

	private void addNestTableRegistHandler(ReferenceProperty p, List<Entity> list, EntityDefinition red, PropertyItem property) {
		// ネストテーブルはプロパティ単位で登録可否決定
		if (!NestTableReferenceRegistHandler.canRegist(property, getRegistrationPropertyBaseHandler())) return;

		ReferencePropertyEditor editor = (ReferencePropertyEditor) property.getEditor();

		List<Entity> target = null;
		if (StringUtil.isNotBlank(editor.getTableOrderPropertyName())) {
			//表示順再指定
			PropertyDefinition pd = red.getProperty(editor.getTableOrderPropertyName());
			target = EntityViewUtil.sortByOrderProperty(list, Constants.REF_TABLE_ORDER_INDEX);
			for (int i = 0; i < target.size(); i++) {
				target.get(i).setValue(editor.getTableOrderPropertyName(), ConvertUtil.convert(pd.getJavaType(), i));
			}
		} else {
			target = list;
		}

		ReferenceRegistHandler handler = NestTableReferenceRegistHandler.get(this, list, red, p, property, editor.getNestProperties(), getRegistrationPropertyBaseHandler());
		if (handler != null) {
			handler.setForceUpdate(editor.isForceUpadte());
			getReferenceRegistHandlers().add(handler);
		}
	}

	/**
	 * Entityにデータが設定されているかチェックします。
	 * @param entity 画面で入力されたデータ
	 * @return Entityにデータが設定されているか
	 */
	private boolean isEmpty(Entity entity) {
		for (PropertyDefinition pd : getPropertyList()) {
			if (pd.getMultiplicity() != 1) {
				Object[] obj = entity.getValue(pd.getName());
				if (obj != null && obj.length > 0) return false;
			} else {
				if (entity.getValue(pd.getName()) != null) return false;
			}
		}
		return true;
	}

	@Override
	protected String getInterrupterName() {
		return getView().getInterrupterName();
	}

	@Override
	protected String getLoadEntityInterrupterName() {
		return getView().getLoadEntityInterrupterName();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RegistrationPropertyBaseHandler<PropertyItem> createRegistrationPropertyBaseHandler() {
		return new RegistrationPropertyBaseHandler<PropertyItem>() {
			@Override
			public boolean isDispProperty(PropertyItem property) {
				return property.isDispFlag();
			}

			@Override
			public PropertyEditor getEditor(PropertyItem property) {
				return property.getEditor();
			}
		};
	}

	public Set<String> getOids() {
		return bulkCommandParams.stream().map(p -> p.getOid()).collect(Collectors.toSet());
	}

	/**
	 * 一括更新画面用のFormレイアウト情報を取得します。
	 * @return 一括更新画面用のFormレイアウト情報
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BulkFormView getView() {
		String viewName = getViewName();
		if (view == null) {
			view = FormViewUtil.getBulkFormView(entityDefinition, entityView, viewName);
		}
		return view;
	}

	/**
	 * 編集画面用のFormレイアウト情報を設定します。
	 * @param view 編集画面用のFormレイアウト情報
	 */
	public void setView(BulkFormView view) {
		this.view = view;
	}

	/**
	 * 新しいバージョンとして更新を行うかを取得します。
	 * @return 新しいバージョンとして更新を行うか
	 */
	@Override
	public boolean isNewVersion() {
		return false;
	}

	@Override
	protected boolean isPurgeCompositionedEntity() {
		return getView().isPurgeCompositionedEntity();
	}

	@Override
	protected boolean isLocalizationData() {
		return getView().isLocalizationData();
	}

	@Override
	protected boolean isForceUpadte() {
		return getView().isForceUpadte();
	}

	/**
	 * 更新可能な被参照（ネストテーブル）を定義内に保持しているかを取得します。
	 * @return
	 */
	@Override
	public boolean hasUpdatableMappedByReference() {
		List<PropertyItem> properties = getProperty();
		for (PropertyItem property : properties) {
			PropertyDefinition pd = getProperty(property.getPropertyName());
			if (pd instanceof ReferenceProperty) {
				String mappedBy = ((ReferenceProperty) pd).getMappedBy();
				if (StringUtil.isBlank(mappedBy)) continue;

				if (property.getEditor() instanceof ReferencePropertyEditor) {
					ReferencePropertyEditor editor = (ReferencePropertyEditor) property.getEditor();
					if (editor.getDisplayType() == ReferenceDisplayType.NESTTABLE) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * リクエストから検索条件を取得します。
	 * @return 検索条件
	 */
	public String getSearchCond() {
		return getParam(Constants.SEARCH_COND);
	}

	/**
	 * リクエストから処理タイプを取得します。
	 * @return
	 */
	public String getExecType() {
		return getParam(Constants.EXEC_TYPE);
	}

	public Integer getRow(String oid, Long version) {
		Integer row = null;
		// バージョン管理されているエンティティでマルチリファレンスのプロパティ定義がある場合、同じOIDとバージョンで行番号が異なるデータが存在するので、
		// 一括更新する際に、一行目だけ更新します。
		Optional<Integer> rw = bulkCommandParams.stream()
				.filter(p -> p.getOid().equals(oid) && p.getVersion().equals(version))
				.map(p -> p.getRow())
				.findFirst();
		if (rw.isPresent()) {
			row = rw.get();
		}
		return row;
	}

	public Set<Long> getVersions(String oid) {
		// バージョン管理の場合、同じOIDで異なるバージョンが存在するので、
		// バージョンセットを取得する。
		return bulkCommandParams.stream()
				.filter(p -> p.getOid().equals(oid))
				.map(p -> p.getVersion())
				.collect(Collectors.toSet());
	}

	public Timestamp getTimestamp(String oid, Long version) {
		Timestamp ts = null;
		// バージョン管理されているエンティティでマルチリファレンスのプロパティ定義がある場合、同じOIDとバージョンで行番号が異なるデータが存在するので、
		// 一括更新する際に、一行目だけ更新します。
		Optional<Long> updateDate = bulkCommandParams.stream()
				.filter(p -> p.getOid().equals(oid) && p.getVersion().equals(version))
				.map(p -> p.getUpdateDate())
				.findFirst();
		if (updateDate.isPresent() && updateDate.get() != null) {
			ts = new Timestamp(updateDate.get());
		}
		return ts;
	}

	public Boolean getSelectAllPage() {
		return request.getParamAsBoolean(Constants.BULK_UPDATE_SELECT_ALL_PAGE);
	}

	public String getSelectAllType() {
		return getParam(Constants.BULK_UPDATE_SELECT_TYPE);
	}

	public Object getBulkUpdatePropertyValue(String propertyName) {
		PropertyDefinition p = entityDefinition.getProperty(propertyName);
		return getPropValue(p, "");
	}

	public Entity createEntity() {
		return createEntityInternal("", null);
	}

	public Entity createEntity(String oid, Long version) {
		Entity entity = createEntityInternal("" , null);
		entity.setOid(oid);
		entity.setUpdateDate(getTimestamp(oid, version));
//		if (isVersioned()) {
		// バージョン管理にかかわらず、セットする問題ないかな..
		entity.setVersion(version);
//		}
//		setVirtualPropertyValue(entity);
		getRegistrationInterrupterHandler().dataMapping(entity);
		validate(entity);
		return entity;
	}

	private Entity createEntityInternal(String paramPrefix, String errorPrefix) {
		Entity entity = newEntity();
		for (PropertyDefinition p : getPropertyList()) {
			if (skipProps.contains(p.getName())) continue;
			Object value = getPropValue(p, paramPrefix);
			entity.setValue(p.getName(), value);
			if (errorPrefix != null) {
				String name = paramPrefix + p.getName();
				// Entity生成時にエラーが発生していないかチェックして置き換え
				String errorName = errorPrefix + p.getName();
				getErrors().stream()
					.filter(error -> error.getPropertyName().equals(name))
					.forEach(error -> error.setPropertyName(errorName));
			}
		}
		return entity;
	}

	/**
	 * 標準の入力チェック以外のチェック、PropertyEditor絡みのもの
	 * @param entity
	 */
	protected void validate(Entity entity) {
		List<PropertyItem> properties = getDisplayProperty();
		for (PropertyItem property : properties) {
			if (property.getEditor() instanceof DateRangePropertyEditor) {
				//日付の逆転チェック
				DateRangePropertyEditor editor = (DateRangePropertyEditor) property.getEditor();
				checkDateRange(editor, entity, property.getPropertyName(), editor.getToPropertyName(), "");
			} else if (property.getEditor() instanceof ReferencePropertyEditor) {
				ReferencePropertyEditor editor = (ReferencePropertyEditor) property.getEditor();
				Object val = entity.getValue(property.getPropertyName());

				Entity[] ary = null;
				if (val != null) {
					if (val instanceof Entity) {
						ary = new Entity[] {(Entity) val};
					} else if (val instanceof Entity[]) {
						ary = (Entity[]) val;
					}
				}

				if (editor.getDisplayType() == ReferenceDisplayType.NESTTABLE
						&& ary != null && ary.length > 0
						&& editor.getNestProperties() != null && !editor.getNestProperties().isEmpty()) {
					//NestTable、参照セクション
					for (int i = 0; i < ary.length; i++) {
						String errorPrefix = property.getPropertyName() + "[" + i + "].";
						for (NestProperty np : editor.getNestProperties()) {
							if (np.getEditor() instanceof DateRangePropertyEditor) {
								//日付の逆転チェック
								DateRangePropertyEditor de = (DateRangePropertyEditor) np.getEditor();
								checkDateRange(de, ary[i], np.getPropertyName(), de.getToPropertyName(), errorPrefix);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 日付範囲のチェック
	 * @param editor
	 * @param entity
	 * @param fromName
	 * @param toName
	 * @param errorPrefix
	 */
	private void checkDateRange(DateRangePropertyEditor editor, Entity entity, String fromName, String toName, String errorPrefix) {
		java.util.Date from = entity.getValue(fromName);
		java.util.Date to = entity.getValue(editor.getToPropertyName());
		if (from != null && to != null && from.compareTo(to) >= 0) {
			String errorMessage = TemplateUtil.getMultilingualString(editor.getErrorMessage(), editor.getLocalizedErrorMessageList());
			if (StringUtil.isBlank(errorMessage )) {
				errorMessage = resourceString("command.generic.bulk.BulkCommandContext.invalitDateRange");
			}
			ValidateError e = new ValidateError();
			e.setPropertyName(errorPrefix + fromName + "_" + editor.getToPropertyName());//fromだけだとメッセージが変なとこに出るので細工
			e.addErrorMessage(errorMessage);
			getErrors().add(e);
		}
	}

	/**
	 * 一括更新するプロパティを取得します。 組み合わせで使うプロパティである場合、通常のプロパティ扱いにします。
	 *
	 * @return 一括更新するプロパティ
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PropertyItem> getProperty() {
		List<PropertyItem> propList = new ArrayList<PropertyItem>();
		for (Section section : getView().getSections()) {
			if (section instanceof DefaultSection) {
				if (section.isDispFlag()) {
					propList.addAll(getProperty((DefaultSection) section));
				}
			}
		}

		for (PropertyDefinition pd : getPropertyList()) {
			String propName = pd.getName();
			if (skipProps.contains(propName)) continue;
			// TODO ブランクの項目は未入力と見なし、更新しません。
			if (getBulkUpdatePropertyValue(propName) == null) {
				propList.removeIf(pi -> pi.getPropertyName().equals(propName));
			}
		}
		return propList;
	}

	/**
	 * セクション内のプロパティ取得を取得します。
	 * @param section セクション
	 * @return プロパティの一覧
	 */
	@SuppressWarnings("unchecked")
	protected List<PropertyItem> getProperty(DefaultSection section) {
		List<PropertyItem> propList = new ArrayList<PropertyItem>();
		for (Element elem : section.getElements()) {
			if (elem instanceof PropertyItem) {
				PropertyItem prop = (PropertyItem) elem;
				if (getRegistrationPropertyBaseHandler().isDispProperty(prop)) {
					if (prop.getEditor() instanceof JoinPropertyEditor) {
						//組み合わせで使うプロパティを通常のプロパティ扱いに
						JoinPropertyEditor je = (JoinPropertyEditor) prop.getEditor();
						for (NestProperty nest : je.getProperties()) {
							PropertyItem dummy = new PropertyItem();
							dummy.setDispFlag(true);
							dummy.setPropertyName(nest.getPropertyName());
							dummy.setEditor(nest.getEditor());
							propList.add(dummy);
						}
					} else if (prop.getEditor() instanceof DateRangePropertyEditor) {
						//組み合わせで使うプロパティを通常のプロパティ扱いに
						DateRangePropertyEditor de = (DateRangePropertyEditor) prop.getEditor();
						PropertyItem dummy = new PropertyItem();
						dummy.setDispFlag(true);
						dummy.setPropertyName(de.getToPropertyName());
						dummy.setEditor(de.getEditor());
						propList.add(dummy);
					}
					propList.add(prop);
				}
			} else if (elem instanceof DefaultSection) {
				if (elem.isDispFlag()) {
					propList.addAll(getProperty((DefaultSection) elem));
				}
			}
		}
		return propList;
	}
	
	/**
	 * 表示プロパティを取得します。
	 * @return プロパティの一覧
	 */
	private List<PropertyItem> getDisplayProperty() {
		List<PropertyItem> propList = new ArrayList<PropertyItem>();

		for (Section section : getView().getSections()) {
			if (!section.isDispFlag()) continue;

			if (section instanceof DefaultSection) {
				DefaultSection ds = (DefaultSection) section;
				propList.addAll(getDisplayProperty(ds));
			}
		}
		return propList;
	}

	/**
	 * セクション内の表示プロパティ取得を取得します。
	 * @param section セクション
	 * @return プロパティの一覧
	 */
	private List<PropertyItem> getDisplayProperty(DefaultSection section) {
		List<PropertyItem> propList = new ArrayList<PropertyItem>();

		for (Element elem : section.getElements()) {
			if (!elem.isDispFlag()) continue;

			if (elem instanceof PropertyItem) {
				PropertyItem prop = (PropertyItem) elem;
				if (prop.getEditor() instanceof JoinPropertyEditor) {
					//組み合わせで使うプロパティを通常のプロパティ扱いに
					JoinPropertyEditor je = (JoinPropertyEditor) prop.getEditor();
					for (NestProperty nest : je.getProperties()) {
						PropertyItem dummy = new PropertyItem();
						dummy.setDispFlag(true);
						dummy.setPropertyName(nest.getPropertyName());
						dummy.setEditor(nest.getEditor());
						propList.add(dummy);
					}
				}
				propList.add(prop);
			} else if (elem instanceof DefaultSection) {
				DefaultSection ds = (DefaultSection) elem;
				propList.addAll(getDisplayProperty(ds));
			}
		}
		return propList;
	}

	@SuppressWarnings("unused")
	private class BulkCommandParams {

		private Map<String, Object> params;

		public BulkCommandParams(Integer row, String oid) {
			setRow(row);
			setOid(oid);
		}

		public Integer getRow() {
			return getValue(Constants.ID);
		}

		public void setRow(Integer row) {
			setValue(Constants.ID, row);
		}

		public String getOid() {
			return getValue(Constants.OID);
		}

		public void setOid(String oid) {
			setValue(Constants.OID, oid);
		}

		public Long getVersion() {
			return getValue(Constants.VERSION);
		}

		public void setVersion(Long version) {
			setValue(Constants.VERSION, version);
		}

		public Long getUpdateDate() {
			return getValue(Constants.TIMESTAMP);
		}

		public void setUpdateDate(Long updateDate) {
			setValue(Constants.TIMESTAMP, updateDate);
		}

		public void setValue(String name, Object value) {
			if (value == null && getValue(name) == null) {
				return;
			}
			if (params == null) {
				params = new HashMap<String, Object>();
			}

			if (value == null) {
				params.remove(name);
			} else {
				params.put(name, value);
			}
		}

		@SuppressWarnings("unchecked")
		public <T> T getValue(String name) {
			if (params != null) {
				return (T) params.get(name);
			}
			return null;
		}

		@Override
		public String toString() {
			return "BulkCommandParams [row=" + getRow() + ", oid=" + getOid() + ", version=" + getVersion() + ", updateDate=" + getUpdateDate() + "]";
		}
	}
}
