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

package org.iplass.gem.command.generic.search.condition;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StrTokenizer;
import org.iplass.gem.command.Constants;
import org.iplass.gem.command.generic.search.SearchConditionDetail;
import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.entity.definition.properties.AutoNumberProperty;
import org.iplass.mtp.entity.definition.properties.BinaryProperty;
import org.iplass.mtp.entity.definition.properties.BooleanProperty;
import org.iplass.mtp.entity.definition.properties.DateProperty;
import org.iplass.mtp.entity.definition.properties.DateTimeProperty;
import org.iplass.mtp.entity.definition.properties.DecimalProperty;
import org.iplass.mtp.entity.definition.properties.ExpressionProperty;
import org.iplass.mtp.entity.definition.properties.FloatProperty;
import org.iplass.mtp.entity.definition.properties.IntegerProperty;
import org.iplass.mtp.entity.definition.properties.LongTextProperty;
import org.iplass.mtp.entity.definition.properties.ReferenceProperty;
import org.iplass.mtp.entity.definition.properties.SelectProperty;
import org.iplass.mtp.entity.definition.properties.StringProperty;
import org.iplass.mtp.entity.definition.properties.TimeProperty;
import org.iplass.mtp.entity.query.condition.Condition;
import org.iplass.mtp.entity.query.condition.expr.Not;
import org.iplass.mtp.entity.query.condition.expr.Paren;
import org.iplass.mtp.entity.query.condition.predicate.Equals;
import org.iplass.mtp.entity.query.condition.predicate.Greater;
import org.iplass.mtp.entity.query.condition.predicate.GreaterEqual;
import org.iplass.mtp.entity.query.condition.predicate.In;
import org.iplass.mtp.entity.query.condition.predicate.IsNotNull;
import org.iplass.mtp.entity.query.condition.predicate.IsNull;
import org.iplass.mtp.entity.query.condition.predicate.Lesser;
import org.iplass.mtp.entity.query.condition.predicate.LesserEqual;
import org.iplass.mtp.entity.query.condition.predicate.Like;
import org.iplass.mtp.entity.query.condition.predicate.NotEquals;
import org.iplass.mtp.view.generic.editor.DateRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.NumericRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.PropertyEditor;
import org.iplass.mtp.view.generic.element.property.PropertyItem;

public abstract class PropertySearchCondition {

	/** プロパティ定義 */
	private PropertyDefinition definition;

	/** プロパティエディタ */
	private PropertyEditor editor;

	/** 検索用の値 */
	private Object value;

	private String parent;

	/**
	 * コンストラクタ
	 * @param definition プロパティ定義
	 * @param editor プロパティエディタ
	 * @param value 検索用の値
	 */
	public PropertySearchCondition(PropertyDefinition definition, PropertyEditor editor, Object value) {
		this.definition = definition;
		this.editor = editor;
		this.value = value;
	}

	/**
	 * コンストラクタ
	 * @param definition プロパティ定義
	 * @param editor プロパティエディタ
	 * @param value 検索用の値
	 */
	public PropertySearchCondition(PropertyDefinition definition, PropertyEditor editor, Object value, String parent) {
		this.definition = definition;
		this.editor = editor;
		this.value = value;
		this.parent = parent;
	}

	/**
	 * 通常検索の条件を生成します。
	 * @return
	 */
	public List<Condition> convertNormalCondition() {
		List<Condition> conditions = new ArrayList<Condition>();
		conditions.add(new Equals(getPropertyName(), getValue()));
		return conditions;
	}

	/**
	 * 詳細検索の条件を生成します。
	 * @return
	 */
	public List<Condition> convertDetailCondition() {
		List<Condition> conditions = new ArrayList<Condition>();
		if (value instanceof SearchConditionDetail) {
			SearchConditionDetail detail = (SearchConditionDetail) value;
			Object conditionValue = convertDetailValue(detail);
			if (Constants.EQUALS.equals(detail.getPredicate())) {
				conditions.add(new Equals(detail.getPropertyName(), conditionValue));
			} else if (Constants.NOTEQUALS.equals(detail.getPredicate())) {
				conditions.add(new NotEquals(detail.getPropertyName(), conditionValue));
			} else if (Constants.GREATER.equals(detail.getPredicate())) {
				conditions.add(new Greater(detail.getPropertyName(), conditionValue));
			} else if (Constants.GREATEREQUALS.equals(detail.getPredicate())) {
				conditions.add(new GreaterEqual(detail.getPropertyName(), conditionValue));
			} else if (Constants.LESSER.equals(detail.getPredicate())) {
				conditions.add(new Lesser(detail.getPropertyName(), conditionValue));
			} else if (Constants.LESSEREQUALS.equals(detail.getPredicate())) {
				conditions.add(new LesserEqual(detail.getPropertyName(), conditionValue));
			} else if (Constants.FRONTMATCH.equals(detail.getPredicate())) {
				//conditions.add(new Like(detail.getPropertyName(), conditionValue + "%"));
				conditions.add(new Like(detail.getPropertyName(), (String) conditionValue, Like.MatchPattern.PREFIX));
			} else if (Constants.BACKWARDMATCH.equals(detail.getPredicate())) {
				//conditions.add(new Like(detail.getPropertyName(), "%" + conditionValue));
				conditions.add(new Like(detail.getPropertyName(), (String) conditionValue, Like.MatchPattern.POSTFIX));
			} else if (Constants.INCLUDE.equals(detail.getPredicate())) {
				//conditions.add(new Like(detail.getPropertyName(), "%" + conditionValue + "%"));
				conditions.add(new Like(detail.getPropertyName(), (String) conditionValue, Like.MatchPattern.PARTIAL));
			} else if (Constants.NOTINCLUDE.equals(detail.getPredicate())) {
				//not not xxx like zzzとならないようにparenで囲む
				//conditions.add(new Paren(new Not(new Like(detail.getPropertyName(), "%" + conditionValue + "%"))));
				conditions.add(new Paren(new Not(new Like(detail.getPropertyName(), (String) conditionValue, Like.MatchPattern.PARTIAL))));
			} else if (Constants.NOTNULL.equals(detail.getPredicate())) {
				conditions.add(new IsNotNull(detail.getPropertyName()));
			} else if (Constants.NULL.equals(detail.getPredicate())) {
				conditions.add(new IsNull(detail.getPropertyName()));
			} else if (Constants.IN.equals(detail.getPredicate())) {
				Object[] array = null;
				if (conditionValue.getClass().isArray()) {
					array = (Object[]) conditionValue;
				} else {
					array = new Object[]{ conditionValue };
				}
				conditions.add(new In(detail.getPropertyName(), array));
			}
		}
		return conditions;
	}

	protected Object convertDetailValue(SearchConditionDetail detail) {
		if (Constants.NOTNULL.equals(detail.getPredicate())
				|| Constants.NULL.equals(detail.getPredicate())) {
			return null;
		} else if (Constants.IN.equals(detail.getPredicate())) {
			StrTokenizer st = StrTokenizer.getCSVInstance(detail.getValue());
			String[] values = st.getTokenArray();
			String[] ret = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				ret[i] = values[i];
			}
			return ret;
		} else if (Constants.FRONTMATCH.equals(detail.getPredicate())
				|| Constants.BACKWARDMATCH.equals(detail.getPredicate())
				|| Constants.INCLUDE.equals(detail.getPredicate())
				|| Constants.NOTINCLUDE.equals(detail.getPredicate())) {
			//return StringUtil.escapeEqlForLike(detail.getValue());
			return detail.getValue();
		} else {
			return detail.getValue();
		}
	}

	protected String getPropertyName() {
		if (parent != null) {
			return parent + "." + definition.getName();
		} else {
			return definition.getName();
		}
	}

	public boolean checkNormalParameter(PropertyItem property) {
		return true;
	}

	public boolean checkDetailParameter(PropertyItem property) {
		if (!(getValue() instanceof SearchConditionDetail)) return false;
		return true;
	}

	/**
	 * プロパティ定義を取得します。
	 * @return プロパティ定義
	 */
	public PropertyDefinition getDefinition() {
	    return definition;
	}

	/**
	 * プロパティ定義を設定します。
	 * @param definition プロパティ定義
	 */
	public void setDefinition(PropertyDefinition definition) {
	    this.definition = definition;
	}

	/**
	 * プロパティエディタを取得します。
	 * @return プロパティエディタ
	 */
	public PropertyEditor getEditor() {
	    return editor;
	}

	/**
	 * プロパティエディタを設定します。
	 * @param editor プロパティエディタ
	 */
	public void setEditor(PropertyEditor editor) {
	    this.editor = editor;
	}

	/**
	 * 検索用の値を取得します。
	 * @return 検索用の値
	 */
	public Object getValue() {
	    return value;
	}

	/**
	 * 検索用の値を設定します。
	 * @param value 検索用の値
	 */
	public void setValue(Object value) {
	    this.value = value;
	}

	/**
	 * 親プロパティ名を取得します。
	 * @return 親プロパティ名
	 */
	protected String getParent() {
		return parent;
	}

	public static PropertySearchCondition newInstance(PropertyDefinition definition,
			PropertyEditor editor, Object value) {
		return newInstance(definition, editor, value, null);
	}
	public static PropertySearchCondition newInstance(PropertyDefinition definition,
			PropertyEditor editor, Object value, String parent) {
		if (definition instanceof AutoNumberProperty) {
			return new AutoNumberPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof BinaryProperty) {
			return new BinaryPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof BooleanProperty) {
			return new BooleanPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof DateProperty) {
			if(editor instanceof DateRangePropertyEditor) {
				return new DateRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new DatePropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof DateTimeProperty) {
			if(editor instanceof DateRangePropertyEditor) {
				return new DateRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new TimestampPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof DecimalProperty) {
			if(editor instanceof NumericRangePropertyEditor) {
				return new NumericRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new DecimalPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof ExpressionProperty) {
			return new ExpressionPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof FloatProperty) {
			if(editor instanceof NumericRangePropertyEditor) {
				return new NumericRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new FloatPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof IntegerProperty) {
			if(editor instanceof NumericRangePropertyEditor) {
				return new NumericRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new IntegerPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof LongTextProperty) {
			return new LongTextPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof ReferenceProperty) {
			return new ReferencePropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof SelectProperty) {
			return new SelectPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof StringProperty) {
			return new StringPropertySearchCondition(definition, editor, value, parent);
		} else if (definition instanceof TimeProperty) {
			if(editor instanceof DateRangePropertyEditor) {
				return new DateRangePropertySearchCondition(definition, editor, value, parent);
			}
			return new TimePropertySearchCondition(definition, editor, value, parent);
		}
		return null;
	}

}
