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

package org.iplass.gem.command.generic.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.iplass.gem.command.CommandUtil;
import org.iplass.gem.command.Constants;
import org.iplass.gem.command.GemResourceBundleUtil;
import org.iplass.gem.command.ViewUtil;
import org.iplass.gem.command.common.SearchResultData;
import org.iplass.gem.command.generic.search.handler.CheckPermissionLimitConditionOfEditLinkHandler;
import org.iplass.gem.command.generic.search.handler.CreateSearchResultEvent;
import org.iplass.gem.command.generic.search.handler.CreateSearchResultEventHandler;
import org.iplass.mtp.ManagerLocator;
import org.iplass.mtp.command.RequestContext;
import org.iplass.mtp.entity.Entity;
import org.iplass.mtp.entity.EntityRuntimeException;
import org.iplass.mtp.entity.definition.EntityDefinition;
import org.iplass.mtp.entity.definition.EntityDefinitionManager;
import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.entity.definition.VersionControlType;
import org.iplass.mtp.entity.definition.properties.ReferenceProperty;
import org.iplass.mtp.entity.query.AsOf;
import org.iplass.mtp.entity.query.From;
import org.iplass.mtp.entity.query.Limit;
import org.iplass.mtp.entity.query.OrderBy;
import org.iplass.mtp.entity.query.Query;
import org.iplass.mtp.entity.query.Select;
import org.iplass.mtp.entity.query.SortSpec;
import org.iplass.mtp.entity.query.SortSpec.NullOrderingSpec;
import org.iplass.mtp.entity.query.SortSpec.SortType;
import org.iplass.mtp.entity.query.condition.Condition;
import org.iplass.mtp.entity.query.value.primary.Literal;
import org.iplass.mtp.impl.util.ObjectUtil;
import org.iplass.mtp.util.StringUtil;
import org.iplass.mtp.utilityclass.definition.UtilityClassDefinitionManager;
import org.iplass.mtp.view.generic.EntityView;
import org.iplass.mtp.view.generic.EntityViewManager;
import org.iplass.mtp.view.generic.EntityViewUtil;
import org.iplass.mtp.view.generic.FormViewUtil;
import org.iplass.mtp.view.generic.NullOrderType;
import org.iplass.mtp.view.generic.OutputType;
import org.iplass.mtp.view.generic.SearchFormView;
import org.iplass.mtp.view.generic.SearchFormViewHandler;
import org.iplass.mtp.view.generic.SearchQueryContext;
import org.iplass.mtp.view.generic.SearchQueryInterrupter;
import org.iplass.mtp.view.generic.SearchQueryInterrupter.SearchQueryType;
import org.iplass.mtp.view.generic.editor.JoinPropertyEditor;
import org.iplass.mtp.view.generic.editor.NestProperty;
import org.iplass.mtp.view.generic.editor.PropertyEditor;
import org.iplass.mtp.view.generic.editor.RangePropertyEditor;
import org.iplass.mtp.view.generic.editor.ReferencePropertyEditor;
import org.iplass.mtp.view.generic.editor.UserPropertyEditor;
import org.iplass.mtp.view.generic.element.property.PropertyColumn;
import org.iplass.mtp.view.generic.element.property.PropertyItem;
import org.iplass.mtp.view.generic.element.section.SearchConditionSection;
import org.iplass.mtp.view.generic.element.section.SearchConditionSection.ConditionSortType;
import org.iplass.mtp.view.generic.element.section.SearchResultSection;
import org.iplass.mtp.view.generic.element.section.SearchResultSection.ExclusiveControlPoint;
import org.iplass.mtp.view.generic.element.section.SortSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SearchContextBase implements SearchContext, CreateSearchResultEventHandler {

	private static Logger log = LoggerFactory.getLogger(SearchContextBase.class);

	private RequestContext request;
	private EntityDefinition definition;
	private EntityView view;
	private SearchFormView form;
	private Set<String> useUserPropertyEditorPropertyNameList;
	private SearchQueryInterrupterHandler interrupterHandler;
	private List<SearchFormViewHandler> searchFormViewHandlers;

	private EntityViewManager evm;
	private UtilityClassDefinitionManager ucdm;

	public SearchContextBase() {
		super();

		evm = ManagerLocator.manager(EntityViewManager.class);
		ucdm = ManagerLocator.manager(UtilityClassDefinitionManager.class);
	}

	@Override
	public RequestContext getRequest() {
		return request;
	}

	@Override
	public void setRequest(RequestContext request) {
		this.request = request;
	}

	@Override
	public EntityDefinition getEntityDefinition() {
		return definition;
	}

	@Override
	public void setEntityDefinition(EntityDefinition definition) {
		this.definition = definition;
	}

	@Override
	public EntityView getEntityView() {
		return view;
	}

	@Override
	public void setEntityView(EntityView view) {
		this.view = view;
	}

	@Override
	public String getDefName() {
		return request.getParam(Constants.DEF_NAME);
	}

	@Override
	public Select getSelect() {
		ArrayList<String> select = new ArrayList<>();
		select.add(Entity.OID);
		select.add(Entity.NAME);
		select.add(Entity.VERSION);

		if (getResultSection().getExclusiveControlPoint() == ExclusiveControlPoint.WHEN_SEARCH) {
			select.add(Entity.UPDATE_DATE);
		}

		List<PropertyColumn> properties = getColumnProperties();
		for (PropertyColumn p : properties) {
			if (EntityViewUtil.isDisplayElement(getDefName(), p.getElementRuntimeId(), OutputType.SEARCHRESULT, null)) {
				String propName = p.getPropertyName();
				if (p.getEditor() instanceof ReferencePropertyEditor) {
					List<NestProperty> nest = ((ReferencePropertyEditor)p.getEditor()).getNestProperties();
					addSearchProperty(select, propName, p.getEditor(), nest.toArray(new NestProperty[nest.size()]));
				} else if (p.getEditor() instanceof JoinPropertyEditor) {
					JoinPropertyEditor je = (JoinPropertyEditor) p.getEditor();
					addSearchProperty(select, propName, je.getEditor());
					for (NestProperty nest : je.getProperties()) {
						addSearchProperty(select, nest.getPropertyName(), nest.getEditor());
					}
				} else if (p.getEditor() instanceof RangePropertyEditor) {
					addSearchProperty(select, propName);
					RangePropertyEditor re = (RangePropertyEditor) p.getEditor();
					if (StringUtil.isNotBlank(re.getToPropertyName())) {
						addSearchProperty(select, re.getToPropertyName());
					}
				} else {
					addSearchProperty(select, propName);
				}
			}
		}
		// ソート条件のデータを取得カラムにしておかないと、DistinctでSQLエラーになる。
		OrderBy orderBy = getOrderBy();
		if (orderBy != null) {
			for (SortSpec sortSpec : orderBy.getSortSpecList()) {
				String sortKey = sortSpec.getSortKey().toString();
				if (!select.contains(sortKey)) addSearchProperty(select, sortKey);
			}
		}
		boolean distinct = getConditionSection().isDistinct();
		Select s = new Select().add(select.toArray());
		s.setDistinct(distinct);
		return s;
	}

	@Override
	public From getFrom() {
		From from = new From(getDefName());

		//バージョン管理されてるEntityでリクエストパラメータに対象とするバージョンがあるか確認
		Object specValue = null;
		String paramSpec = getRequest().getParam(Constants.SEARCH_SPEC_VERSION);
		if (definition.getVersionControlType() != null && StringUtil.isNotBlank(paramSpec)) {
			if (VersionControlType.TIMEBASE.equals(definition.getVersionControlType())
					|| VersionControlType.SIMPLE_TIMEBASE.equals(definition.getVersionControlType())) {
				//日時
				specValue = CommandUtil.getTimestamp(paramSpec);
			} else if (VersionControlType.VERSIONED.equals(definition.getVersionControlType())
					|| VersionControlType.STATEBASE.equals(definition.getVersionControlType())) {
				//数値
				specValue = CommandUtil.getLong(paramSpec);
			}
		}

		//リテラル化してAsOfに設定
		if (specValue != null) {
			AsOf asof = new AsOf(new Literal(specValue));
			from.setAsOf(asof);
		}

		return from;
	}

	@Override
	public OrderBy getOrderBy() {
		OrderBy orderBy = null;
		// ソート設定が存在する場合
		if (hasSortSetting()) {
			orderBy = new OrderBy();
			for (SortSetting ss : getSortSetting()) {
				String sortKey = ss.getSortKey();
				PropertyDefinition pd = getPropertyDefinition(sortKey);
				// ソートキーに参照プロパティ自体が指定された場合（参照先Entityのプロパティまで明示指定された場合は除く）
				if (pd instanceof ReferenceProperty) {
					PropertyColumn property = getLayoutPropertyColumn(sortKey);
					// 当該項目が画面上表示される場合は、画面上の表示項目でソート
					if (property != null) {
						sortKey = sortKey + "." + getDisplayNestProperty(property);
					} else {
						// 画面上に表示されない場合は、Nameでソート
						sortKey = sortKey + "." + Entity.NAME;
					}
				}
				SortType type = SortType.valueOf(ss.getSortType().name());
				NullOrderingSpec nullOrderingSpec = getNullOrderingSpec(ss.getNullOrderType());
				orderBy.add(sortKey, type, nullOrderingSpec);
			}
		} else {
			// ソート設定がない場合
			String sortKey = getSortKey();
			if (Entity.OID.equals(sortKey)) {
				orderBy = new OrderBy();
				orderBy.add(sortKey, getSortType());
			} else {
				PropertyColumn property = getLayoutPropertyColumn(sortKey);
				// OID以外はSearchResultに定義されているPropertyのみ許可
				if (property != null) {
					PropertyDefinition pd = getPropertyDefinition(sortKey);
					// 参照プロパティの場合、画面上の表示項目でソート
					if (pd instanceof ReferenceProperty) {
						sortKey = sortKey + "." + getDisplayNestProperty(property);
					}
					NullOrderingSpec nullOrderingSpec = getNullOrderingSpec(property.getNullOrderType());
					orderBy = new OrderBy();
					orderBy.add(sortKey, getSortType(), nullOrderingSpec);
				}
			}
		}
		return orderBy;
	}
	@Override
	public Limit getLimit() {
		Limit limit = new Limit(getSearchLimit(), getOffset());
		return limit;
	}

	@Override
	public boolean isVersioned() {
		String allVer = getRequest().getParam("allVersion");
		return "1".equals(allVer);
	}

	@Override
	public boolean isSearch() {
		String isSearch = getRequest().getParam("isSearch");
		return "true".equals(isSearch);
	}

	@Override
	public boolean isCount() {
		String isCount = getRequest().getParam("isCount");
		return "true".equals(isCount);
	}

	@Override
	public boolean validation() {
		return true;
	}

	protected SearchFormView getForm() {
		String viewName = getViewName();
		if (form == null) {
			form = FormViewUtil.getSearchFormView(definition, view, viewName);
		}
		return form;
	}

	/**
	 * 検索条件セクションを取得します。
	 * @return 検索条件セクション
	 */
	protected SearchConditionSection getConditionSection() {
		return getForm() != null ? getForm().getCondSection() : null;
	}

	/**
	 * 検索条件プロパティを取得します。
	 * @return
	 */
	protected List<PropertyItem> getLayoutProperties() {
		List<PropertyItem> filteredList = getConditionSection().getElements().stream()
				.filter(e -> e instanceof PropertyItem).map(e -> (PropertyItem) e)
				.collect(Collectors.toList());

		List<PropertyItem> properties = new ArrayList<>();
		for (PropertyItem property : filteredList) {
			if (property.getEditor() instanceof RangePropertyEditor) {
				//範囲系の場合FromとToを分離しておく
				RangePropertyEditor editor = (RangePropertyEditor) property.getEditor();
				PropertyItem from = ObjectUtil.deepCopy(property);
				properties.add(from);

				PropertyItem to = ObjectUtil.deepCopy(property);
				to.setPropertyName(editor.getToPropertyName());
				properties.add(to);
			} else {
				properties.add(property);
			}
		}

		return properties;
	}

	/**
	 * 検索結果セクションを取得します。
	 * @return 検索結果セクション
	 */
	protected SearchResultSection getResultSection() {
		return getForm() != null ? getForm().getResultSection() : null;
	}

	/**
	 * 検索結果プロパティを取得します。
	 * @return
	 */
	protected List<PropertyColumn> getColumnProperties() {
		List<PropertyColumn> properties = getResultSection().getElements().stream()
				.filter(e -> e instanceof PropertyColumn).map(e -> (PropertyColumn) e)
				.collect(Collectors.toList());
		return properties;
	}

	protected PropertyItem getLayoutProperty(String propName) {
		List<PropertyItem> properties = getLayoutProperties();
		Optional<PropertyItem> property = properties.stream().filter(e -> propName.equals(e.getPropertyName())).findFirst();
		if (property.isPresent()) {
			return property.get();
		}
		return null;
	}

	protected PropertyItem getLayoutPropertyForCheck(String propName) {
		List<PropertyItem> properties = getLayoutProperties();
		for (PropertyItem property : properties) {
			String condPropName = property.getPropertyName();

			if (StringUtil.isNotEmpty(condPropName) && condPropName.contains(".")) {
				condPropName = condPropName.split("\\.")[0];
			}

			if (propName.equals(condPropName)) return property;
		}
		return null;
	}

	protected PropertyColumn getLayoutPropertyColumn(String propName) {
		Optional<PropertyColumn> property = getColumnProperties().stream()
				.filter(e -> propName.equals(e.getPropertyName())).findFirst();
		if (property.isPresent()) {
			return property.get();
		}
		return null;
	}

	/**
	 * プロパティ定義の一覧を取得します。
	 * @return プロパティ定義の一覧
	 */
	protected List<PropertyDefinition> getPropertyList() {
		return definition.getPropertyList();
	}

	protected Condition getDefaultCondition() {
		SearchConditionSection section = getConditionSection();
		if (section == null || StringUtil.isEmpty(section.getDefaultCondition())) {
			return null;
		}

		return evm.getSearchConditionSectionDefaultCondition(getDefName(), section);
	}

	protected String getViewName() {
		return request.getParam(Constants.VIEW_NAME);
	}
	/**
	 * リクエストからソートキーを取得します。
	 * ソートキーが指定されていない場合は、検索画面のデフォルトソートキーを取得します。
	 * @return ソートキー
	 */
	protected String getSortKey() {
		String sortKey = request.getParam(Constants.SEARCH_SORTKEY);
		
		// 検索時のソートキー
		if (StringUtil.isBlank(sortKey)) {
			if (getConditionSection().isUnsorted()) {
				return null;
			}
			// デフォルトはOID
			return Entity.OID;
		}
		
		PropertyDefinition pd = getPropertyDefinition(sortKey);
		if (pd == null) {
			//ソート項目が存在しない場合はOIDを設定
			return Entity.OID;
		}
		
		return sortKey;
	}

	/**
	 * リクエストからソートタイプを取得します。
	 * ソート種別が指定されていない場合は検索画面のデフォルトソートタイプを取得します。
	 * @return ソートタイプ
	 */
	protected SortType getSortType() {
		String sortType = request.getParam(Constants.SEARCH_SORTTYPE);
		if (StringUtil.isBlank(sortType)) {
			return SortType.DESC;
		}
		return SortType.valueOf(sortType);
	}

	protected NullOrderingSpec getNullOrderingSpec(NullOrderType type) {
		if (type == null) return null;
		switch (type) {
		case FIRST:
			return NullOrderingSpec.FIRST;
		case LAST:
			return NullOrderingSpec.LAST;
		default:
			break;
		}
		return null;
	}

	/**
	 * ソート設定が定義されているか
	 * @return ソート設定が定義されているか
	 */
	protected boolean hasSortSetting() {
		SearchConditionSection section = getConditionSection();
		if (section != null) {
			return !section.getSortSetting().isEmpty();
		}
		return false;
	}

	/**
	 * ソート設定を取得します。
	 * @return ソート設定
	 */
	protected List<SortSetting> getSortSetting() {
		List<SortSetting> setting = new ArrayList<>();

		//画面でソート条件が指定されれば第1キーに
		String sortKey = getRequest().getParam(Constants.SEARCH_SORTKEY);
		if (StringUtil.isNotBlank(sortKey)) {
			PropertyColumn property = getLayoutPropertyColumn(sortKey);
			// SearchResultに定義されているPropertyのみ許可
			if (property != null) {
				SortSetting ss = new SortSetting();
				ss.setSortKey(sortKey);
				
				String sortType = getRequest().getParam(Constants.SEARCH_SORTTYPE);
				if (StringUtil.isBlank(sortType)) {
					ss.setSortType(ConditionSortType.DESC);
				} else {
					ss.setSortType(ConditionSortType.valueOf(sortType));
				}
				
				ss.setNullOrderType(property.getNullOrderType());
				
				setting.add(ss);
			}
		}
		
		SearchConditionSection section = getConditionSection();
		if (section != null && !section.getSortSetting().isEmpty()) {
			setting.addAll(section.getSortSetting());
		}
		return setting;
	}
	
	/**
	 * 参照プロパティで、検索結果に表示されている項目を取得します。
	 * @return 表示項目
	 */
	protected String getDisplayNestProperty(PropertyColumn refProp) {
		PropertyEditor editor = refProp.getEditor();
		
		if (editor instanceof ReferencePropertyEditor
				&& StringUtil.isNotEmpty(((ReferencePropertyEditor) editor).getDisplayLabelItem())) {
			return ((ReferencePropertyEditor)editor).getDisplayLabelItem();
		} else {
			return Entity.NAME;
		}
		
	}
	
	/**
	 * リクエストから検索上限を取得します。
	 * 検索上限が指定されていない場合は検索画面のデフォルトソート上限を取得します。
	 * @return
	 */
	protected Integer getSearchLimit() {
		Integer limit = request.getParamAsInt(Constants.SEARCH_LIMIT);
		if (limit == null) limit = ViewUtil.getSearchLimit(getResultSection());
		return limit;
	}

	/**
	 * リクエストから現在の検索位置を取得します。
	 * 検索位置が指定されていない場合は0が返ります。
	 * @return
	 */
	protected Integer getOffset() {
		Integer offset = request.getParamAsInt(Constants.SEARCH_OFFSET);
		if (offset == null) offset = 0;
		return offset;
	}

	/**
	 * 検索項目として利用可能な場合にプロパティを検索項目に追加します。
	 * @param context コンテキスト
	 * @param select SELECTするプロパティの一覧
	 * @param propName セットするプロパティ名
	 * @param nest 参照先Entityのプロパティ
	 */
	protected void addSearchProperty(ArrayList<String> select, String propName, NestProperty... nest) {
		addSearchProperty(select, propName, null, nest);
	}

	protected void addSearchProperty(ArrayList<String> select, String propName, PropertyEditor editor, NestProperty... nest) {
		PropertyDefinition pd = getPropertyDefinition(propName);
		if (pd instanceof ReferenceProperty) {
			if (!select.contains(propName + "." + Entity.NAME)) {
				select.add(propName + "." + Entity.NAME);
			}
			if (!select.contains(propName + "." + Entity.OID)) {
				select.add(propName + "." + Entity.OID);
			}
			if (!select.contains(propName + "." + Entity.VERSION)) {
				select.add(propName + "." + Entity.VERSION);
			}
			if (editor instanceof ReferencePropertyEditor) {
				addDisplayLabelProperty(select, propName, (ReferencePropertyEditor) editor);
			}
			if (nest != null && nest.length > 0) {
				EntityDefinition red = getReferenceEntityDefinition((ReferenceProperty) pd);
				for (NestProperty np : nest) {
					PropertyDefinition rpd = red.getProperty(np.getPropertyName());
					if (rpd != null
							&&!Entity.OID.equals(np.getPropertyName())
							&& !Entity.NAME.equals(np.getPropertyName())
							&& !Entity.VERSION.equals(np.getPropertyName())) {
						String nestPropName = propName + "." + np.getPropertyName();
						if (rpd instanceof ReferenceProperty) {
							if (!select.contains(nestPropName + "." + Entity.NAME)) {
								select.add(nestPropName + "." + Entity.NAME);
							}
							if (!select.contains(nestPropName + "." + Entity.OID)) {
								select.add(nestPropName + "." + Entity.OID);
							}
							if (!select.contains(nestPropName + "." + Entity.VERSION)) {
								select.add(nestPropName + "." + Entity.VERSION);
							}
						} else {
							if (!select.contains(nestPropName)) {
								select.add(nestPropName);
							}
						}
						if (np.getEditor() instanceof ReferencePropertyEditor) {
							ReferencePropertyEditor rpe = (ReferencePropertyEditor) np.getEditor();
							if (!rpe.getNestProperties().isEmpty()) {
								List<NestProperty> _nest = rpe.getNestProperties();
								addSearchProperty(select, nestPropName, rpe, _nest.toArray(new NestProperty[_nest.size()]));
							}
							addDisplayLabelProperty(select, nestPropName, rpe);
						} else if (np.getEditor() instanceof JoinPropertyEditor) {
							JoinPropertyEditor jpe = (JoinPropertyEditor) np.getEditor();
							addSearchProperty(select, nestPropName, jpe.getEditor());
							if (!jpe.getProperties().isEmpty()) {
								List<NestProperty> _nest = jpe.getProperties();
								addSearchProperty(select, propName, editor, _nest.toArray(new NestProperty[_nest.size()]));
							}
						} else if (np.getEditor() instanceof RangePropertyEditor) {
							RangePropertyEditor jpe = (RangePropertyEditor) np.getEditor();
							if (jpe.getToPropertyName() != null) {
								select.add(propName + "." + jpe.getToPropertyName());
							}
						}
					}
				}
			}
		} else {
			if (!select.contains(propName)) select.add(propName);
		}
	}

	protected void addDisplayLabelProperty(ArrayList<String> select, String propName, ReferencePropertyEditor rpe) {
		if (rpe.getDisplayLabelItem() == null) return;

		PropertyDefinition pd = getPropertyDefinition(propName);
		if (pd instanceof ReferenceProperty) {
			if (!select.contains(propName + "." + rpe.getDisplayLabelItem())) {
				select.add(propName + "." + rpe.getDisplayLabelItem());
			}
		}
	}

	/**
	 * 参照型プロパティのEntity定義を取得します。
	 * @param rp
	 * @return
	 */
	protected EntityDefinition getReferenceEntityDefinition(ReferenceProperty rp) {
		EntityDefinitionManager edm = ManagerLocator.getInstance().getManager(EntityDefinitionManager.class);
		return edm.get(rp.getObjectDefinitionName());
	}

	protected PropertyDefinition getPropertyDefinition(String propName) {
		int firstDotIndex = propName.indexOf('.');
		if (firstDotIndex > 0) {
			String topPropName = propName.substring(0, firstDotIndex);
			String subPropName = propName.substring(firstDotIndex + 1);
			PropertyDefinition topProperty = definition.getProperty(topPropName);
			if (topProperty instanceof ReferenceProperty) {
				EntityDefinition red = getReferenceEntityDefinition((ReferenceProperty) topProperty);
				if (red != null) {
					//definitionを参照先のdefinitionに置き換えて再帰呼び出し
					EntityDefinition _definition = definition;
					definition = red;
					PropertyDefinition pd = getPropertyDefinition(subPropName);
					//definitionを戻す
					definition = _definition;
					return pd;
				}
			}
		} else {
			return definition.getProperty(propName);
		}
		return null;
	}

	/**
	 * UserPropertyEditorを利用しているか
	 * @return UserPropertyEditorを利用しているか
	 */
	public boolean isUseUserPropertyEditor() {
		Set<String> propNameList = getUseUserPropertyEditorPropertyName();
		return !propNameList.isEmpty();
	}

	/**
	 * UserPropertyEditorを利用しているプロパティ名の一覧を取得します。
	 * @return UserPropertyEditorを利用しているプロパティ名の一覧
	 */
	public Set<String> getUseUserPropertyEditorPropertyName() {
		if (useUserPropertyEditorPropertyNameList == null) {
			//UserPropertyEditorを使用しているlockedbyプロパティを取得
			useUserPropertyEditorPropertyNameList
				= getUseUserPropertyEditorPropertyName(Entity.LOCKED_BY, Entity.CREATE_BY, Entity.UPDATE_BY);
		}
		return useUserPropertyEditorPropertyNameList;
	}

	/**
	 * 指定のプロパティ名を持ち、UserPropertyEditorを利用している検索結果のプロパティ名を取得します。
	 * @param propNames
	 * @return
	 */
	private Set<String> getUseUserPropertyEditorPropertyName(String... propNames) {
		Set<String> ret = new HashSet<>();
		List<PropertyColumn> properties = getColumnProperties();
		for (PropertyColumn property : properties) {
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
	 * 検索処理実行前にクエリに対する操作を行います。
	 * @param query
	 * @return
	 */
	public SearchQueryContext beforeSearch(Query query, SearchQueryType type) {
		return getSearchQueryInterrupterHandler().beforeSearch(query, type);
	}

	/**
	 * 検索処理実行後にカスタム処理を実行します。
	 * @param query
	 * @param resultList
	 * @param type
	 */
	public void afterSearch(Query query, Entity entity, SearchQueryType type) {
		getSearchQueryInterrupterHandler().afterSearch(query, entity, type);
	}

	/**
	 * カスタム登録処理を取得します。
	 * @return カスタム登録処理
	 */
	public SearchQueryInterrupterHandler getSearchQueryInterrupterHandler() {
		if (interrupterHandler == null) {
			SearchQueryInterrupter interrupter = createInterrupter(getForm().getInterrupterName());
			interrupterHandler = new SearchQueryInterrupterHandler(request, this, interrupter);
		}
		return interrupterHandler;
	}

	protected SearchQueryInterrupter createInterrupter(String className) {
		SearchQueryInterrupter interrupter = null;
		if (StringUtil.isNotEmpty(className)) {
			if (log.isDebugEnabled()) {
				log.debug("set search query interrupter. class=" + className);
			}
			try {
				interrupter = ucdm.createInstanceAs(SearchQueryInterrupter.class, className);
			} catch (ClassNotFoundException e) {
				log.error(className + " can not instantiate.", e);
				throw new EntityRuntimeException(resourceString("command.generic.detail.DetailCommandContext.internalErr"));
			}
		}
		if (interrupter == null) {
			//何もしないデフォルトInterrupter生成
			if (log.isDebugEnabled()) {
				log.debug("set defaul search query interrupter.");
			}
			interrupter = getDefaultSearchQueryInterrupter();
		}
		return interrupter;
	}

	protected SearchQueryInterrupter getDefaultSearchQueryInterrupter() {
		return new SearchQueryInterrupter() {};
	}

	@Override
	public void fireCreateSearchResultEvent(final SearchResultData result) {

		CreateSearchResultEvent event = new CreateSearchResultEvent(getRequest(), getDefName(), getViewName(), result);
		for (SearchFormViewHandler handler : getSearchFormViewHandlers()) {
			handler.onCreateSearchResult(event);
		}
	}

	private List<SearchFormViewHandler> getSearchFormViewHandlers() {
		if (searchFormViewHandlers == null) {
			searchFormViewHandlers = new ArrayList<>();
			if (getForm().getSearchFormViewHandlerName() != null) {
				for (String handlerClassName : getForm().getSearchFormViewHandlerName()) {
					searchFormViewHandlers.add(createSearchFormViewHandler(handlerClassName));
				}
			}
			//編集リンクの範囲権限チェックを行う場合は、先頭にHandlerを追加
			if (!getResultSection().isHideDetailLink()
					&& getResultSection().isCheckEntityPermissionLimitConditionOfEditLink()) {
				if (log.isDebugEnabled()) {
					log.debug("add CheckUpdateConditionOfEditLinkHandler to the first of search form view handler.");
				}
				searchFormViewHandlers.add(0, new CheckPermissionLimitConditionOfEditLinkHandler());
			}
		}
		return searchFormViewHandlers;
	}

	private SearchFormViewHandler createSearchFormViewHandler(String handlerClassName) {
		SearchFormViewHandler handler = null;
		if (StringUtil.isNotEmpty(handlerClassName)) {
			if (log.isDebugEnabled()) {
				log.debug("create search form view handler. class=" + handlerClassName);
			}
			try {
				handler = ucdm.createInstanceAs(SearchFormViewHandler.class, handlerClassName);
			} catch (ClassNotFoundException e) {
				log.error(handlerClassName + " can not instantiate.", e);
				throw new EntityRuntimeException(resourceString("command.generic.detail.DetailCommandContext.internalErr"));
			}
		}
		return handler;
	}

	private static String resourceString(String key, Object... arguments) {
		return GemResourceBundleUtil.resourceString(key, arguments);
	}
}
