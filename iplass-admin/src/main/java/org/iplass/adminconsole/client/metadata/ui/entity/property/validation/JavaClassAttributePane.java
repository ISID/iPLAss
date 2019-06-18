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

package org.iplass.adminconsole.client.metadata.ui.entity.property.validation;

import org.iplass.adminconsole.client.base.ui.widget.form.MtpForm;
import org.iplass.adminconsole.client.base.ui.widget.form.MtpTextItem;
import org.iplass.adminconsole.client.base.util.SmartGWTUtil;
import org.iplass.adminconsole.client.metadata.ui.entity.property.ValidationListGridRecord;
import org.iplass.adminconsole.client.metadata.ui.entity.property.ValidationListGridRecord.ValidationType;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class JavaClassAttributePane extends ValidationAttributePane {

	private DynamicForm form;

	private TextItem classNameItem;
	private CheckboxItem bindAsArrayItem;

	public JavaClassAttributePane() {

		classNameItem = new MtpTextItem();
		classNameItem.setTitle("Java Class Name");
		SmartGWTUtil.addHoverToFormItem(classNameItem, rs("ui_metadata_entity_PropertyListGrid_javaClassComment"));

		bindAsArrayItem = new CheckboxItem();
		bindAsArrayItem.setTitle("bind variable to array types");
		SmartGWTUtil.addHoverToFormItem(bindAsArrayItem, rs("ui_metadata_entity_PropertyListGrid_javaClassAsArray"));

		form = new MtpForm();
		form.setItems(classNameItem, bindAsArrayItem);

		addMember(form);
	}

	@Override
	public void setDefinition(ValidationListGridRecord record) {

		classNameItem.setValue(record.getJavaClassName());
		bindAsArrayItem.setValue(record.isAsArray());
	}

	@Override
	public ValidationListGridRecord getEditDefinition(ValidationListGridRecord record) {

		record.setJavaClassName(SmartGWTUtil.getStringValue(classNameItem, true));
		record.setAsArray(SmartGWTUtil.getBooleanValue(bindAsArrayItem));

		return record;
	}

	@Override
	public boolean validate() {
		return form.validate();
	}

	@Override
	public void clearErrors() {
		form.clearErrors(true);
	}

	@Override
	public ValidationType getType() {
		return ValidationType.JAVA_CLASS;
	}

	@Override
	public int panelHeight() {
		return 80;
	}

}
