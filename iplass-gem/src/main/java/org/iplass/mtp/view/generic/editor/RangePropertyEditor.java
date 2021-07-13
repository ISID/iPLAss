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

package org.iplass.mtp.view.generic.editor;

import java.util.List;

import org.iplass.mtp.definition.LocalizedStringDefinition;

/**
 * 範囲型プロパティエディタ
 * @author ICOM Shojima
 */
public interface RangePropertyEditor {

	/**
	 * @return editor
	 */
	public PropertyEditor getEditor();

	/**
	 * @return propertyName
	 */
	public String getPropertyName();

	/**
	 * @return toEditor
	 */
	public PropertyEditor getToEditor();

	/**
	 * @return toPropertyName
	 */
	public String getToPropertyName();

	/**
	 * @return toPropertyDisplayName
	 */
	public String getToPropertyDisplayName();

	/**
	 * @return localizedToPropertyDisplayNameList
	 */
	public List<LocalizedStringDefinition> getLocalizedToPropertyDisplayNameList();

	/**
	 * @return errorMessage
	 */
	public String getErrorMessage();

	/**
	 * @return localizedErrorMessageList
	 */
	public List<LocalizedStringDefinition> getLocalizedErrorMessageList();
}
