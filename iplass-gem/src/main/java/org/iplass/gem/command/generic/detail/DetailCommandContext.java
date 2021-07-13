/*
 * Copyright (C) 2012 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

package org.iplass.gem.command.generic.detail;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.iplass.gem.GemConfigService;
import org.iplass.gem.command.CommandUtil;
import org.iplass.gem.command.Constants;
import org.iplass.gem.command.ViewUtil;
import org.iplass.gem.command.generic.detail.handler.CheckPermissionLimitConditionOfButtonHandler;
import org.iplass.gem.command.generic.detail.handler.ShowDetailLayoutViewEvent;
import org.iplass.gem.command.generic.detail.handler.ShowDetailViewEventHandler;
import org.iplass.gem.command.generic.detail.handler.ShowEditViewEventHandler;
import org.iplass.mtp.command.RequestContext;
import org.iplass.mtp.entity.Entity;
import org.iplass.mtp.entity.EntityManager;
import org.iplass.mtp.entity.EntityRuntimeException;
import org.iplass.mtp.entity.LoadOption;
import org.iplass.mtp.entity.ValidateError;
import org.iplass.mtp.entity.definition.EntityDefinition;
import org.iplass.mtp.entity.definition.EntityDefinitionManager;
import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.entity.definition.properties.BooleanProperty;
import org.iplass.mtp.entity.definition.properties.DateProperty;
import org.iplass.mtp.entity.definition.properties.DateTimeProperty;
import org.iplass.mtp.entity.definition.properties.DecimalProperty;
import org.iplass.mtp.entity.definition.properties.FloatProperty;
import org.iplass.mtp.entity.definition.properties.IntegerProperty;
import org.iplass.mtp.entity.definition.properties.ReferenceProperty;
import org.iplass.mtp.entity.definition.properties.SelectProperty;
import org.iplass.mtp.entity.definition.properties.StringProperty;
import org.iplass.mtp.entity.definition.properties.TimeProperty;
import org.iplass.mtp.impl.util.ConvertUtil;
import org.iplass.mtp.spi.ServiceRegistry;
import org.iplass.mtp.util.StringUtil;
import org.iplass.mtp.view.generic.DetailFormView;
import org.iplass.mtp.view.generic.DetailFormView.CopyTarget;
import org.iplass.mtp.view.generic.DetailFormViewHandler;
import org.iplass.mtp.view.generic.EntityViewUtil;
import org.iplass.mtp.view.generic.FormViewUtil;
import org.iplass.mtp.view.generic.OutputType;
import org.iplass.mtp.view.generic.editor.DateRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.JoinPropertyEditor;
import org.iplass.mtp.view.generic.editor.NestProperty;
import org.iplass.mtp.view.generic.editor.NumericRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.PropertyEditor;
import org.iplass.mtp.view.generic.editor.RangePropertyEditor;
import org.iplass.mtp.view.generic.editor.ReferencePropertyEditor;
import org.iplass.mtp.view.generic.editor.ReferencePropertyEditor.ReferenceDisplayType;
import org.iplass.mtp.view.generic.editor.UserPropertyEditor;
import org.iplass.mtp.view.generic.element.Element;
import org.iplass.mtp.view.generic.element.VirtualPropertyItem;
import org.iplass.mtp.view.generic.element.property.PropertyItem;
import org.iplass.mtp.view.generic.element.section.DefaultSection;
import org.iplass.mtp.view.generic.element.section.ReferenceSection;
import org.iplass.mtp.view.generic.element.section.Section;
import org.iplass.mtp.web.template.TemplateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 詳細コマンド関連の情報を管理するクラス。
 * @author lis3wg
 */
public class DetailCommandContext extends RegistrationCommandContext
		implements ShowDetailViewEventHandler, ShowEditViewEventHandler {

	private static Logger logger = LoggerFactory.getLogger(DetailCommandContext.class);

	/** 編集画面用のFormレイアウト情報 */
	private DetailFormView view;

	private List<DetailFormViewHandler> detailFormViewHandlers;

	private GemConfigService gemConfig = null;

	private Set<String> useUserPropertyEditorPropertyNameList;

	/** 更新対象ロードエンティティ*/
	private Entity currentEntity;
	/** 編集エンティティ*/
	private Entity editedEntity;

	@Override
	protected Logger getLogger() {
		return logger;
	}

	/**
	 * コンストラクタ
	 * @param request リクエスト
	 */
	public DetailCommandContext(RequestContext request, EntityManager entityLoader,
			EntityDefinitionManager definitionLoader) {
		super(request, entityLoader, definitionLoader);

		init();
	}

	public DetailCommandContext(RequestContext request, String defName, String viewName, EntityManager entityLoader,
			EntityDefinitionManager definitionLoader) {
		super(request, defName, viewName, entityLoader, definitionLoader);

		init();
	}

	private void init() {
		gemConfig = ServiceRegistry.getRegistry().getService(GemConfigService.class);
	}

	/**
	 * 編集画面用のFormレイアウト情報を取得します。
	 * @return 編集画面用のFormレイアウト情報
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DetailFormView getView() {
		String viewName = getViewName();
		if (view == null) {
			view = FormViewUtil.getDetailFormView(entityDefinition, entityView, viewName);
		}
		return view;
	}

	/**
	 * 編集画面用のFormレイアウト情報を設定します。
	 * @param view 編集画面用のFormレイアウト情報
	 */
	public void setView(DetailFormView view) {
		this.view = view;
	}

	/*
	 * 表示判定用Bindエンティティを取得します。
	 */
	public Entity getDispControlBindEntity() {
		if (logger.isTraceEnabled()) {
			logger.trace("Bind display control entity is :" + (editedEntity != null ? "edited" : "loaded"));
		}
		return editedEntity != null ? editedEntity : currentEntity;
	}


	/**
	 * 更新対象ロードエンティティを設定します。
	 * @param currentEntity 対象エンティティ
	 */
	public void setCurrentEntity(Entity currentEntity) {
		this.currentEntity = currentEntity;
	}

	/**
	 * 編集エンティティを設定します。
	 * @param editedEntity 編集エンティティ
	 */
	protected void setEditedEntity(Entity editedEntity) {
		this.editedEntity = editedEntity;
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
				//詳細編集で非表示なら更新対象外
				return EntityViewUtil.isDisplayElement(entityDefinition.getName(), property.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !property.isHideDetail();
			}

			@Override
			public PropertyEditor getEditor(PropertyItem property) {
				return property.getEditor();
			}
		};
	}

	/**
	 * フォーム内のプロパティを取得します。
	 * @param view 画面定義
	 * @return プロパティの一覧
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PropertyItem> getProperty() {
		String execType = getExecType();
		List<PropertyItem> propList = new ArrayList<>();
		for (Section section : getView().getSections()) {
			if (section instanceof DefaultSection) {
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !((DefaultSection) section).isHideDetail() && ViewUtil.dispElement(execType, section)) {
					propList.addAll(getProperty((DefaultSection) section));
				}
			} else if (section instanceof ReferenceSection) {
				// 参照セクションは同一名の定義が複数の場合があるのでまとめる
				ReferenceSection rs = (ReferenceSection) section;
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !rs.isHideDetail() && ViewUtil.dispElement(execType, rs)) {
					Optional<ReferenceSectionPropertyItem> ret = propList.stream().filter(p -> p instanceof ReferenceSectionPropertyItem)
						.map(p -> (ReferenceSectionPropertyItem) p)
						.filter(p -> p.getPropertyName().equals(rs.getPropertyName()))
						.findFirst();

					if (ret.isPresent()) {
						ret.get().getSections().add(rs);
					} else {
						propList.add(createReferenceSectionPropertyItem(rs));
					}
				}
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
	private List<PropertyItem> getProperty(DefaultSection section) {
		String execType = getExecType();
		List<PropertyItem> propList = new ArrayList<>();
		for (Element elem : section.getElements()) {
			if (elem instanceof PropertyItem) {
				PropertyItem prop = (PropertyItem) elem;
				if (getRegistrationPropertyBaseHandler().isDispProperty(prop) && ViewUtil.dispElement(execType, prop)) {
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
					} else if (prop.getEditor() instanceof RangePropertyEditor) {
						//組み合わせで使うプロパティを通常のプロパティ扱いに
						RangePropertyEditor de = (RangePropertyEditor) prop.getEditor();
						PropertyItem dummy = new PropertyItem();
						dummy.setDispFlag(true);
						dummy.setPropertyName(de.getToPropertyName());
						dummy.setEditor(de.getEditor());
						propList.add(dummy);
					}
					propList.add(prop);
				}
			} else if (elem instanceof DefaultSection) {
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), elem.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !((DefaultSection) elem).isHideDetail() && ViewUtil.dispElement(execType, elem)) {
					propList.addAll(getProperty((DefaultSection) elem));
				}
			} else if (elem instanceof ReferenceSection) {
				// 参照セクションは同一名の定義が複数の場合があるのでまとめる
				ReferenceSection rs = (ReferenceSection) elem;
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), elem.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !rs.isHideDetail() && ViewUtil.dispElement(execType, rs)) {
					Optional<ReferenceSectionPropertyItem> ret = propList.stream().filter(p -> p instanceof ReferenceSectionPropertyItem)
						.map(p -> (ReferenceSectionPropertyItem) p)
						.filter(p -> p.getPropertyName().equals(rs.getPropertyName()))
						.findFirst();

					if (ret.isPresent()) {
						ret.get().getSections().add(rs);
					} else {
						propList.add(createReferenceSectionPropertyItem(rs));
					}
				}
			}
		}
		return propList;
	}

	/**
	 * 参照セクションからプロパティを取得します。
	 * @param section セクション
	 * @return プロパティ
	 */
	private ReferenceSectionPropertyItem createReferenceSectionPropertyItem(ReferenceSection section) {
		ReferenceSectionPropertyItem property = new ReferenceSectionPropertyItem();
		property.setPropertyName(section.getPropertyName());
//		property.setDispFlag(section.getDispFlag());
		property.getSections().add(section);
		return property;
	}

	/**
	 * 表示対象の参照プロパティ名を取得します
	 * @return
	 */
	public List<String> getReferencePropertyName() {
		//表示しているプロパティだけとりだす。表示/編集は分からないのでとりあえず両方。
		Set<String> loadReferences = new HashSet<>();
		for (Section section : getView().getSections()) {
			if (section instanceof DefaultSection) {
				getReferencePropertyName((DefaultSection)section, loadReferences);
			} else if (section instanceof ReferenceSection) {
				ReferenceSection rs = (ReferenceSection) section;
				loadReferences.add(rs.getPropertyName());
			}
		}
		return new ArrayList<>(loadReferences);
	}

	private void getReferencePropertyName(DefaultSection section, Set<String> loadReferences) {
		for (Element element : section.getElements()) {
			if (element instanceof PropertyItem) {
				PropertyItem property = (PropertyItem) element;
				PropertyDefinition pd = getProperty(property.getPropertyName());
				if (pd instanceof ReferenceProperty) {
					//大量データ用のPropertyEditorを使わない参照プロパティのみ
					loadReferences.add(property.getPropertyName());
				}
				if (property.getEditor() instanceof JoinPropertyEditor) {
					JoinPropertyEditor jpe = (JoinPropertyEditor) property.getEditor();
					for (NestProperty nest : jpe.getProperties()) {
						//JoinPropertyエディターのネストプロパティでの参照プロパティ
						if (nest.getEditor() instanceof ReferencePropertyEditor) {
							loadReferences.add(nest.getPropertyName());
						}
					}
				}
			} else if (element instanceof DefaultSection) {
				getReferencePropertyName((DefaultSection) element, loadReferences);
			} else if (element instanceof ReferenceSection) {
				ReferenceSection rs = (ReferenceSection) element;
				loadReferences.add(rs.getPropertyName());
			}
		}
	}

	// 同一プロパティの参照セクションをまとめておくPropertyItem
	public class ReferenceSectionPropertyItem extends PropertyItem {

		private static final long serialVersionUID = -2527968770756351039L;

		private List<ReferenceSection> sections = new ArrayList<>();

		public List<ReferenceSection> getSections() {
			return sections;
		}

		public ReferenceSection getFirstSection() {
			return sections.size() > 0 ? sections.get(0) : null;
		}

		public ReferenceSection getSection(int index) {
			Optional<ReferenceSection> ret = sections.stream().filter(s -> s.getIndex() == index).findFirst();
			if (ret.isPresent()) {
				return ret.get();
			}
			return null;
		}

		@Override
		public boolean isDispFlag() {
			// dispFlagがfalseのものがあればfalse扱い
			for (ReferenceSection section : sections) {
				if (!EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public boolean isHideDetail() {
			// hideDetailがfalseのものがあればfalse扱い
			for (ReferenceSection section : sections) {
				if (!section.isHideDetail()) return false;
			}
			return true;
		}
	}

	/**
	 * リクエストからOIDを取得します。
	 * @return OID
	 */
	public String getOid() {
		return getParam(Constants.OID);
	}

	/**
	 * リクエストからバージョンを取得します。
	 * @return バージョン
	 */
	public Long getVersion() {
		return getLongValue(Constants.VERSION);
	}

	/**
	 * リクエストからフィルタ名を取得します。
	 * @return フィルタ名
	 */
	public String getFilterName() {
		return getParam(Constants.FILTER_NAME);
	}

	/**
	 * コピーを行うかを取得します。
	 * @return コピーを行うか
	 */
	public boolean isCopy() {
		String copy = getParam(Constants.COPY);
		return copy != null && "true".equals(copy);
	}

	public CopyTarget getCopyTarget() {
		String copyTarget = getParam("copyTarget");
		if (copyTarget == null || copyTarget.isEmpty()) {
			return CopyTarget.SHALLOW;
		}
		return CopyTarget.getEnum(copyTarget);
	}

	/**
	 * 新しいバージョンとして更新を行うかを取得します。
	 * @return 新しいバージョンとして更新を行うか
	 */
	@Override
	public boolean isNewVersion() {
		String newVersion = getParam(Constants.NEWVERSION);
		return newVersion != null && "true".equals(newVersion);
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
	 * 更新可能な被参照（ネストテーブル、参照セクション）を定義内に保持しているかを取得します。
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

				if (property instanceof ReferenceSectionPropertyItem) {
					return true;
				} else if (property.getEditor() instanceof ReferencePropertyEditor) {
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

	/**
	 * ロードしたデータをリクエストパラメータの値で上書きするかを取得します。
	 * @return
	 */
	public boolean isUpdateByParam() {
		String updateByParam = getParam("updateByParam");
		return updateByParam != null && "true".equals(updateByParam);
	}

	/**
	 * リクエストのパラメータからEntiyを作成します。
	 * @return 画面で入力したデータ
	 */
	public Entity createEntity() {
		Entity entity = createEntity("", null);
		entity.setUpdateDate(getTimestamp());
		if (isVersioned()) {
			entity.setVersion(getVersion());
		}
		setVirtualPropertyValue(entity);
		getRegistrationInterrupterHandler().dataMapping(entity);
		validate(entity);
		//FIXME 更新の時のみ対象エンティティを設定します。
		setEditedEntity(entity);
		return entity;
	}

	/**
	 * リクエストのパラメータからEntiyを作成します。
	 * @param paramPrefix 参照型のプロパティのリクエストパラメータに設定されているプレフィックス
	 * @param errorPrefix エラーが発生した場合にValidateErrorにセットするプレフィックス
	 * @return 画面で入力したデータ
	 */
	private Entity createEntity(String paramPrefix, String errorPrefix) {
		Entity entity = newEntity();
		for (PropertyDefinition p : getPropertyList()) {
			Object value = getPropValue(p, paramPrefix);
			entity.setValue(p.getName(), value);

			if (errorPrefix != null) {
				String name = paramPrefix + p.getName();
				//Entity生成時にエラーが発生していないかチェックして置き換え
				String errorName = errorPrefix +  p.getName();
				getErrors().stream()
					.filter(error -> error.getPropertyName().equals(name))
					.forEach(error -> error.setPropertyName(errorName));
			}
		}
		return entity;
	}

	private Timestamp getTimestamp() {
		Timestamp ts = null;
		Long l = getLongValue(Constants.TIMESTAMP);
		if (l != null) ts = new Timestamp(l);
		return ts;
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

		//NestTable、ReferenceSectionの場合の件数取得
		//prefixが付くケース=NestTable内の多重参照なのであり得ない
		//→件数取れないため通常の参照扱いで処理が終わる
		Long count = getLongValue(prefix + p.getName() + "_count");
		String isRs = getParam("isReferenceSection_" + prefix + p.getName());
		if (p.getMultiplicity() == 1) {
			Entity entity = null;
			if (count == null) {
				String key = getParam(prefix + p.getName());
				entity = getRefEntity(rp.getObjectDefinitionName(), key);
			} else {
				List<Entity> list = null;
				if (isRs != null && "true".equals(isRs)) {
					list = getRefSectionValues(rp, defName, count, prefix);
				} else {
					list = getRefTableValues(rp, defName, count, prefix);
				}
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
				if (isRs != null && "true".equals(isRs)) {
					list = getRefSectionValues(rp, defName, count, prefix);
				} else {
					list = getRefTableValues(rp, defName, count, prefix);
				}
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
		final List<Entity> list = new ArrayList<>();
		EntityDefinition ed = getEntityDefinition();
		EntityDefinition red = definitionManager.get(defName);
		setEntityDefinition(red);//参照先の定義に詰め替える
		for (int i = 0; i < count; i++) {
			//データあり
			String paramPrefix = prefix + p.getName() + "[" + Integer.toString(i) + "].";
			String errorPrefix = (i != list.size() ? prefix + p.getName() + "[" + Integer.toString(list.size()) + "]." : null);
			Entity entity = createEntity(paramPrefix, errorPrefix);

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
		Optional<PropertyItem> ret = getProperty().stream().filter(pi -> pi.getPropertyName().equals(p.getName())).findFirst();
		if (ret.isPresent()) {
			addNestTableRegistHandler(p, list, red, ret.get());
		}

		return list;
	}

	private void addNestTableRegistHandler(ReferenceProperty p, List<Entity> list, EntityDefinition red, PropertyItem property) {
		// ネストテーブルはプロパティ単位で登録可否決定
		if (!NestTableReferenceRegistHandler.canRegist(property, getRegistrationPropertyBaseHandler())) return;

		// カスタム登録処理によるNestTableの更新制御
		ReferenceRegistOption option = null;
		if (currentEntity != null) {
			option = getRegistrationInterrupterHandler().getNestTableRegistOption(red, p.getName());

			// isSpecifyAllPropertiesがtrue、且つReference項目が除外、且つNestされたEntityの個々プロパティも全て指定なしの場合
			// 参照先EntityにおけるEntityのデータ追加、更新不可
			if (option.isSpecifyAllProperties() && !option.isSpecifiedAsReference()
					&& option.getSpecifiedUpdateNestProperties().isEmpty()) {
				return;
			}
		}

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

		ReferenceRegistHandler handler = NestTableReferenceRegistHandler.get(this, list, red, p, property, editor.getNestProperties(), getRegistrationPropertyBaseHandler(), option);
		if (handler != null) {
			handler.setForceUpdate(editor.isForceUpadte());
			getReferenceRegistHandlers().add(handler);
		}
	}


	private List<Entity> getRefSectionValues(ReferenceProperty p, String defName, Long count, String prefix) {
		List<Entity> list = new ArrayList<>();
		EntityDefinition ed = getEntityDefinition();
		EntityDefinition red = definitionManager.get(defName);
		setEntityDefinition(red);//参照先の定義に詰め替える

		List<PropertyItem> properties = getProperty();
		List<ReferenceSectionValue> valList = new ArrayList<>();

		Optional<ReferenceSectionPropertyItem> ret = properties.stream()
				.filter(pi -> pi instanceof ReferenceSectionPropertyItem)
				.map(pi -> (ReferenceSectionPropertyItem) pi)
				.filter(pi -> pi.getPropertyName().equals(p.getName())).findFirst();
		ReferenceSectionPropertyItem rsProperty = ret.isPresent() ? ret.get() : null;

		for (int i = 0; i < count; i++) {
			//データあり
			String paramPrefix = prefix + p.getName() + "[" + Integer.toString(i) + "].";
			String errorPrefix = (i != list.size() ? prefix + p.getName() + "[" + Integer.toString(list.size()) + "]." : null);
			Entity entity = createEntity(paramPrefix, errorPrefix);

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

				if (rsProperty != null) {
					// entity,sectionでまとめる
					if (p.getMultiplicity() == 1) {
						if (rsProperty.getSections().size() > 0) {
							ReferenceSection section = rsProperty.getSections().get(0);
							ReferenceSectionValue rsv = new ReferenceSectionValue();
							rsv.entity = entity;
							rsv.section = section;
							valList.add(rsv);
						}
					} else {
						// 多重度1以外は表示順プロパティとデータのインデックスを考慮
						String idx = getParam("referenceSectionIndex_" + prefix + p.getName() + "[" + Integer.toString(i) + "]");
						int index = CommandUtil.getInteger(idx);
						ReferenceSection section = rsProperty.getSection(index);
						if (section != null) {
							ReferenceSectionValue rsv = new ReferenceSectionValue();
							rsv.index = index;
							rsv.entity = entity;
							rsv.section = section;
							valList.add(rsv);
						}
					}
				}

				list.add(entity);
			}
		}
		setEntityDefinition(ed);//元の定義に詰め替える

		// 参照セクション用の登録処理を追加
		if (rsProperty != null) {
			addReferenceSectionRegistHandler(p, valList, red, rsProperty);
		}

		return list;
	}

	private void addReferenceSectionRegistHandler(final ReferenceProperty p, final List<ReferenceSectionValue> list, EntityDefinition red, ReferenceSectionPropertyItem property) {
		// 参照セクションはプロパティ単位で登録可否決定
		if (!ReferenceSectionReferenceRegistHandler.canRegist(property)) return;

		// カスタム登録処理による参照セクションの更新制御
		ReferenceRegistOption option = null;
		if (currentEntity != null) {
			option = getRegistrationInterrupterHandler().getNestTableRegistOption(red, p.getName());

			// isSpecifyAllPropertiesがtrue、且つReference項目が除外、且つNestされたEntityの個々プロパティも全て指定なしの場合
			// 参照先EntityにおけるEntityのデータ更新不可
			if (option.isSpecifyAllProperties() && !option.isSpecifiedAsReference()
					&& option.getSpecifiedUpdateNestProperties().isEmpty()) {
				return;
			}
		}

		ReferenceRegistHandler handler = ReferenceSectionReferenceRegistHandler.get(this, list, red, p, property, option);
		if (handler != null) {
			//handler.setForceUpdate(forceUpadte); //参照セクションはSection毎に個別設定になるので、Handler内で設定
			getReferenceRegistHandlers().add(handler);
		}
	}

	public class ReferenceSectionValue {
		private Integer index;
		private Entity entity;
		private ReferenceSection section;
		public Integer getIndex() {
			return index;
		}
		public Entity getEntity() {
			return entity;
		}
		public void setEntity(Entity entity) {
			this.entity = entity;
		}
		public ReferenceSection getSection() {
			return section;
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

	private void setVirtualPropertyValue(Entity entity) {
		List<VirtualPropertyItem> virtualProperties = getVirtualProperty();
		for (VirtualPropertyItem property : virtualProperties) {
			PropertyDefinition p = EntityViewUtil.getPropertyDefinition(property);
			Object value = null;
			boolean isMultiple = p.getMultiplicity() != 1;//現状必ず1
			String name = p.getName();
			if (p instanceof BooleanProperty) {
				value = isMultiple ? getBooleanValues(name, p.getMultiplicity()) : getBooleanValue(name);
			} else if (p instanceof DateProperty) {
				value = isMultiple ? getDateValues(name) : getDateValue(name);
			} else if (p instanceof DateTimeProperty) {
				value = isMultiple ? getTimestampValues(name) : getTimestampValue(name);
			} else if (p instanceof DecimalProperty) {
				value = isMultiple ? getDecimalValues(name) : getDecimalValue(name);
			} else if (p instanceof FloatProperty) {
				value = isMultiple ? getDoubleValues(name) : getDoubleValue(name);
			} else if (p instanceof IntegerProperty) {
				value = isMultiple ? getLongValues(name) : getLongValue(name);
			} else if (p instanceof SelectProperty) {
				value = isMultiple ? getSelectValues(name) : getSelectValue(name);
			} else if (p instanceof StringProperty) {
				value = isMultiple ? getStringValues(name) : getStringValue(name);
			} else if (p instanceof TimeProperty) {
				value = isMultiple ? getTimeValues(name) : getTimeValue(name);
			}

			entity.setValue(p.getName(), value);
		}
	}

	/**
	 * フォーム内の仮想プロパティを取得します。
	 * @param view 画面定義
	 * @return 仮想プロパティの一覧
	 */
	private List<VirtualPropertyItem> getVirtualProperty() {
		String execType = getExecType();
		List<VirtualPropertyItem> propList = new ArrayList<>();
		for (Section section : getView().getSections()) {
			if (section instanceof DefaultSection) {
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !((DefaultSection) section).isHideDetail() && ViewUtil.dispElement(execType, section)) {
					propList.addAll(getVirtualProperty((DefaultSection) section));
				}
			}
		}
		return propList;
	}

	/**
	 * セクション内の仮想プロパティ取得を取得します。
	 * @param section セクション
	 * @return 仮想プロパティの一覧
	 */
	private List<VirtualPropertyItem> getVirtualProperty(DefaultSection section) {
		String execType = getExecType();
		List<VirtualPropertyItem> propList = new ArrayList<>();
		for (Element elem : section.getElements()) {
			if (elem instanceof VirtualPropertyItem) {
				VirtualPropertyItem prop = (VirtualPropertyItem) elem;
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), prop.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !prop.isHideDetail() && ViewUtil.dispElement(execType, prop)) {
					propList.add(prop);
				}
			} else if (elem instanceof DefaultSection) {
				if (EntityViewUtil.isDisplayElement(getDefinitionName(), elem.getElementRuntimeId(), OutputType.EDIT, getDispControlBindEntity())
						&& !((DefaultSection) elem).isHideDetail() && ViewUtil.dispElement(execType, elem)) {
					propList.addAll(getVirtualProperty((DefaultSection) elem));
				}
			}
		}
		return propList;
	}

	/**
	 * UserPropertyEditorを利用しているか
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @return UserPropertyEditorを利用しているか
	 */
	public boolean isUseUserPropertyEditor(boolean isDetail) {
		Set<String> propNameList = getUseUserPropertyEditorPropertyName(isDetail);
		return !propNameList.isEmpty();
	}

	/**
	 * UserPropertyEditorを利用しているプロパティ名の一覧を取得します。
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @return UserPropertyEditorを利用しているプロパティ名の一覧
	 */
	public Set<String> getUseUserPropertyEditorPropertyName(boolean isDetail) {

		if (useUserPropertyEditorPropertyNameList == null) {
			useUserPropertyEditorPropertyNameList
				= getUseUserPropertyEditorPropertyName(isDetail, Entity.LOCKED_BY, Entity.CREATE_BY, Entity.UPDATE_BY);
		}
		return useUserPropertyEditorPropertyNameList;
	}

	/**
	 * 指定のプロパティ名を持ち、UserPropertyEditorを利用している検索結果のプロパティ名を取得します。
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @param propNames
	 * @return
	 */
	private Set<String> getUseUserPropertyEditorPropertyName(boolean isDetail, String... propNames) {

		Set<String> ret = new HashSet<>();
		for (PropertyItem property : getDisplayProperty(isDetail)) {
			String propertyName = property.getPropertyName();

			if (property.getEditor() instanceof ReferencePropertyEditor) {
				//ネストの項目を確認
				ReferencePropertyEditor editor = (ReferencePropertyEditor) property.getEditor();
				if (!editor.getNestProperties().isEmpty()) {
					Set<String> nest = getUseUserPropertyEditorNestPropertyName(editor, propNames);
					for (String nestPropertyName : nest) {
						String _nestPropertyName = propertyName + "." + nestPropertyName;
						ret.add(_nestPropertyName);
					}
				}
			} else if (property.getEditor() instanceof UserPropertyEditor) {
				//直接指定項目の確認
				for (String propName : propNames) {
					boolean isUserEditor = false;
					if (propertyName.contains(".")) {
						//ReferencePropertyの直接指定
						isUserEditor = propertyName.endsWith("." + propName);
					} else {
						//通常Property
						isUserEditor = propertyName.equals(propName);
					}
					if (isUserEditor) {
						ret.add(propertyName);
					}
				}
			}

		}
		return ret;
	}

	private Set<String> getUseUserPropertyEditorNestPropertyName(ReferencePropertyEditor editor, String... propNames) {

		Set<String> ret = new HashSet<>();
		for (NestProperty property : editor.getNestProperties()) {

			if (property.getEditor() instanceof ReferencePropertyEditor) {
				//再ネストの項目を確認
				ReferencePropertyEditor nestEditor = (ReferencePropertyEditor) property.getEditor();
				if (!nestEditor.getNestProperties().isEmpty()) {
					Set<String> nest = getUseUserPropertyEditorNestPropertyName(nestEditor, propNames);
					for (String nestPropertyName : nest) {
						String _nestPropertyName = property.getPropertyName() + "." + nestPropertyName;
						ret.add(_nestPropertyName);
					}
				}
			} else if (property.getEditor() instanceof UserPropertyEditor) {
				//NestProperty項目
				for (String propName : propNames) {
					if (propName.equals(property.getPropertyName())) {
						ret.add(property.getPropertyName());
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 標準の入力チェック以外のチェック、PropertyEditor絡みのもの
	 * @param entity
	 */
	protected void validate(Entity entity) {
		List<PropertyItem> properties = getDisplayProperty(true);
		for (PropertyItem property : properties) {
			if (property.getEditor() instanceof DateRangePropertyEditor) {
				//日付の逆転チェック
				DateRangePropertyEditor editor = (DateRangePropertyEditor) property.getEditor();
				checkDateRange(editor, entity, property.getPropertyName(), editor.getToPropertyName(), "");
			} else if (property.getEditor() instanceof NumericRangePropertyEditor) {
				//数値の逆転チェック
				NumericRangePropertyEditor editor = (NumericRangePropertyEditor) property.getEditor();
				checkNumericRange(editor, entity, property.getPropertyName(), editor.getToPropertyName(), "");
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
							} else if (np.getEditor() instanceof NumericRangePropertyEditor) {
								//数値の逆転チェック
								NumericRangePropertyEditor de = (NumericRangePropertyEditor) np.getEditor();
								checkNumericRange(de, ary[i], np.getPropertyName(), de.getToPropertyName(), errorPrefix);
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

		if (from == null && to != null) {
			if (!editor.isInputNullFrom()) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.inputDateRangeErr");
			}
		} else if (from != null && to == null) {
			if (!editor.isInputNullTo()) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.inputDateRangeErr");
			}
		} else if (from != null && to != null) {
			boolean result = false;

			if (editor.isEquivalentInput()) {
				result = (from.compareTo(to) > 0) ? true : false;
			} else {
				result = (from.compareTo(to) >= 0) ? true : false;
			}

			if (result) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.invalidDateRange");
			}
		}
	}

	/**
	 * 数値範囲のチェック
	 * @param editor
	 * @param entity
	 * @param fromName
	 * @param toName
	 * @param errorPrefix
	 */
	private void checkNumericRange(NumericRangePropertyEditor editor, Entity entity, String fromName, String toName, String errorPrefix) {
		Number from = entity.getValue(fromName);
		Number to = entity.getValue(editor.getToPropertyName());
		BigDecimal from_tmp = castNumericRangeNumber(from);
		BigDecimal to_tmp = castNumericRangeNumber(to);

		if (from_tmp == null && to_tmp != null) {
			if (!editor.isInputNullFrom()) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.inputNumericRangeErr");
			}
		} else if (from_tmp != null && to_tmp == null) {
			if (!editor.isInputNullTo()) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.inputNumericRangeErr");
			}
		} else if (from_tmp != null && to_tmp != null) {
			boolean result = false;

			if (editor.isEquivalentInput()) {
				result = (from_tmp.compareTo(to_tmp) > 0) ? true : false;
			} else {
				result = (from_tmp.compareTo(to_tmp) >= 0) ? true : false;
			}
			if (result) {
				setValidateErrorMessage(editor, fromName, errorPrefix, "command.generic.detail.DetailCommandContext.invalidNumericRange");
			}
		}
	}

	/**
	 * 数値範囲のキャスト
	 * @param number
	 * @return castNumber
	 */
	private BigDecimal castNumericRangeNumber(Number number) {
		if (number instanceof Double) {
			return new BigDecimal(number.doubleValue());
		} else if (number instanceof Long) {
			return new BigDecimal(number.longValue());
		} else if (number instanceof BigDecimal) {
			return (BigDecimal) number;
		} else {
			return null;
		}
	}

	/**
	 * エラーメッセージの設定
	 * @param editor
	 * @param entity
	 * @param fromName
	 * @param errorPrefix
	 * @param inputNullFlag
	 * @param comparisonFlag
	 */
	private void setValidateErrorMessage(RangePropertyEditor editor, String fromName, String errorPrefix, String resourceStringKey) {
		String errorMessage = TemplateUtil.getMultilingualString(editor.getErrorMessage(), editor.getLocalizedErrorMessageList());
		if (StringUtil.isBlank(errorMessage )) {
			errorMessage = resourceString(resourceStringKey);
		}
		ValidateError e = new ValidateError();
		e.setPropertyName(errorPrefix + fromName + "_" + editor.getToPropertyName());//fromだけだとメッセージが変なとこに出るので細工
		e.addErrorMessage(errorMessage);
		getErrors().add(e);
	}

	/**
	 * 表示プロパティを取得します。
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @return プロパティの一覧
	 */
	private List<PropertyItem> getDisplayProperty(boolean isDetail) {
		List<PropertyItem> propList = new ArrayList<>();

		String execType = getExecType();
		OutputType outputType = isDetail ? OutputType.EDIT : OutputType.VIEW;
		for (Section section : getView().getSections()) {
			if (!EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), outputType, getDispControlBindEntity())) {
				continue;
			}

			if (section instanceof DefaultSection) {
				DefaultSection ds = (DefaultSection) section;
				if ((isDetail && !ds.isHideDetail() && ViewUtil.dispElement(execType, ds)) || (!isDetail && !ds.isHideView())) {
					propList.addAll(getDisplayProperty(ds, isDetail));
				}
			} else if (section instanceof ReferenceSection) {
				ReferenceSection rs = (ReferenceSection) section;
				if ((isDetail && !rs.isHideDetail() && ViewUtil.dispElement(execType, rs)) || (!isDetail && !rs.isHideView())) {
					propList.add(getDisplayProperty(rs, outputType));
				}
			}
		}
		return propList;
	}

	/**
	 * セクション内の表示プロパティ取得を取得します。
	 * @param section セクション
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @return プロパティの一覧
	 */
	private List<PropertyItem> getDisplayProperty(DefaultSection section, boolean isDetail) {
		List<PropertyItem> propList = new ArrayList<>();

		String execType = getExecType();
		OutputType outputType = isDetail ? OutputType.EDIT : OutputType.VIEW;
		for (Element elem : section.getElements()) {
			if (!EntityViewUtil.isDisplayElement(getDefinitionName(), elem.getElementRuntimeId(), outputType, getDispControlBindEntity())) {
				continue;
			}

			if (elem instanceof PropertyItem) {
				PropertyItem prop = (PropertyItem) elem;
				if ((isDetail && !prop.isHideDetail() && ViewUtil.dispElement(execType, prop)) || (!isDetail && !prop.isHideView())) {
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
				}
			} else if (elem instanceof DefaultSection) {
				DefaultSection ds = (DefaultSection) elem;
				if ((isDetail && !ds.isHideDetail() && ViewUtil.dispElement(execType, ds)) || (!isDetail && !ds.isHideView())) {
					propList.addAll(getDisplayProperty(ds, isDetail));
				}
			} else if (elem instanceof ReferenceSection) {
				ReferenceSection rs = (ReferenceSection) elem;
				if ((isDetail && !rs.isHideDetail() && ViewUtil.dispElement(execType, rs)) || (!isDetail && !rs.isHideView())) {
					propList.add(getDisplayProperty(rs, outputType));
				}
			}
		}
		return propList;
	}

	/**
	 * 参照セクションから表示プロパティを取得します。
	 * @param section セクション
	 * @param isDetail true:詳細編集、false:詳細表示
	 * @return プロパティ
	 */
	private PropertyItem getDisplayProperty(ReferenceSection section, OutputType outputType) {

		PropertyItem property = new PropertyItem();
		property.setPropertyName(section.getPropertyName());
		property.setDispFlag(EntityViewUtil.isDisplayElement(getDefinitionName(), section.getElementRuntimeId(), outputType, getDispControlBindEntity()));

		ReferencePropertyEditor editor = new ReferencePropertyEditor();
		editor.setDisplayType(ReferenceDisplayType.NESTTABLE);
		editor.setObjectName(section.getDefintionName());
		editor.setNestProperties(section.getProperties());
		property.setEditor(editor);
		return property;
	}

	public boolean isShallowCopyLobData() {
		return gemConfig.isShallowCopyLobData();
	}

	@Override
	public void fireShowDetailViewEvent(DetailFormViewData detailFormViewData) {

		ShowDetailLayoutViewEvent event = new ShowDetailLayoutViewEvent(getRequest(), getDefinitionName(), getViewName(), detailFormViewData);
		for (DetailFormViewHandler handler : getDetailFormViewHandlers()) {
			handler.onShowDetailView(event);
		}
	}

	@Override
	public void fireShowEditViewEvent(DetailFormViewData detailFormViewData) {

		ShowDetailLayoutViewEvent event = new ShowDetailLayoutViewEvent(getRequest(), getDefinitionName(), getViewName(), detailFormViewData);
		for (DetailFormViewHandler handler : getDetailFormViewHandlers()) {
			handler.onShowEditView(event);
		}
	}

	private List<DetailFormViewHandler> getDetailFormViewHandlers() {
		if (detailFormViewHandlers == null) {
			detailFormViewHandlers = new ArrayList<>();
			if (getView().getDetailFormViewHandlerName() != null) {
				for (String handlerClassName : getView().getDetailFormViewHandlerName()) {
					detailFormViewHandlers.add(createDetailFormViewHandler(handlerClassName));
				}
			}
			//ボタンの範囲権限チェックを行う場合は、先頭にHandlerを追加
			if (getView().isCheckEntityPermissionLimitConditionOfButton()) {
				if (logger.isDebugEnabled()) {
					logger.debug("add CheckPermissionLimitConditionOfButtonHandler to the first of detail form view handler.");
				}
				detailFormViewHandlers.add(0, new CheckPermissionLimitConditionOfButtonHandler());
			}
		}
		return detailFormViewHandlers;
	}

	private DetailFormViewHandler createDetailFormViewHandler(String handlerClassName) {
		DetailFormViewHandler handler = null;
		if (StringUtil.isNotEmpty(handlerClassName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("create detail form view handler. class=" + handlerClassName);
			}
			try {
				handler = ucdm.createInstanceAs(DetailFormViewHandler.class, handlerClassName);
			} catch (ClassNotFoundException e) {
				logger.error(handlerClassName + " can not instantiate.", e);
				throw new EntityRuntimeException(resourceString("command.generic.detail.DetailCommandContext.internalErr"));
			}
		}
		return handler;
	}
}
