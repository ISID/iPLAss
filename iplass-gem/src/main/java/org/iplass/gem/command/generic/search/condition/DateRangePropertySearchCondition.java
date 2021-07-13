/*
 * Copyright (C) 2021 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.entity.query.condition.Condition;
import org.iplass.mtp.entity.query.condition.expr.And;
import org.iplass.mtp.entity.query.condition.expr.Or;
import org.iplass.mtp.view.generic.editor.DateRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.PropertyEditor;

/**
 * 日付範囲型の検索条件
 * @author ICOM Shojima
 */

public class DateRangePropertySearchCondition extends PropertySearchCondition {

	/**
	 * コンストラクタ
	 * @param definition プロパティ定義
	 * @param editor プロパティエディタ
	 * @param value 検索用の値
	 */
	public DateRangePropertySearchCondition(PropertyDefinition definition,
			PropertyEditor editor, Object value) {
		super(definition, editor, value);
	}

	/**
	 * コンストラクタ
	 * @param definition プロパティ定義
	 * @param editor プロパティエディタ
	 * @param value 検索用の値
	 * @param parent 親ノード
	 */
	public DateRangePropertySearchCondition(PropertyDefinition definition,
			PropertyEditor editor, Object value, String parent) {
		super(definition, editor, value, parent);
	}

	/**
	 * 日付範囲型の検索条件設定
	 * @return conditions 検索条件
	 */
	@Override
	public List<Condition> convertNormalCondition() {
		List<Condition> conditions = new ArrayList<>();
		String parentName = "";
		Object[] obl = (Object[]) getValue();
		//From-To検索
		Object val = null;
		boolean inputNullFrom = getEditor().isInputNullFrom();
		boolean inputNullTo = getEditor().isInputNullTo();
		boolean equivalentInput = getEditor().isEquivalentInput();
		Or or = new Or();

		if (obl.length > 0 && obl[0] != null) {
			val = obl[0];
		}
		if (val != null) {
			if (getParent() != null && getEditor().getToPropertyName().indexOf(".") == -1) {
				parentName =  getParent()  + ".";
			}
			or.addExpression(new And().lte(getPropertyName(), val).gt(parentName + getEditor().getToPropertyName(), val));

			if (inputNullFrom) {
				or.addExpression(new And().isNull(getPropertyName()).gt(parentName + getEditor().getToPropertyName(), val));
			}

			if (inputNullTo) {
				or.addExpression(new And().lte(getPropertyName(), val).isNull(parentName + getEditor().getToPropertyName()));
			}

			if (equivalentInput) {
				or.addExpression(new And().eq(getPropertyName(), val).eq(parentName + getEditor().getToPropertyName(), val));
			}
		}
		conditions.add(or);

		return conditions;
	}

	@Override
	public DateRangePropertyEditor getEditor() {
		return (DateRangePropertyEditor) super.getEditor();
	}
}
