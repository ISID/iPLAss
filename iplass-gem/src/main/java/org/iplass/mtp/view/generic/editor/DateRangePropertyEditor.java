/*
 * Copyright (C) 2017 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.iplass.adminconsole.annotation.MultiLang;
import org.iplass.adminconsole.view.annotation.IgnoreField;
import org.iplass.adminconsole.view.annotation.InputType;
import org.iplass.adminconsole.view.annotation.MetaFieldInfo;
import org.iplass.adminconsole.view.annotation.generic.EntityViewField;
import org.iplass.adminconsole.view.annotation.generic.FieldReferenceType;
import org.iplass.mtp.definition.LocalizedStringDefinition;
import org.iplass.mtp.view.generic.Jsp;
import org.iplass.mtp.view.generic.Jsps;
import org.iplass.mtp.view.generic.ViewConst;

@XmlAccessorType(XmlAccessType.FIELD)
@Jsps({
	@Jsp(path="/jsp/gem/generic/editor/DateRangePropertyEditor.jsp", key=ViewConst.DESIGN_TYPE_GEM)
})
@IgnoreField({"customStyle", "inputCustomStyle"})
public class DateRangePropertyEditor extends CustomPropertyEditor implements RangePropertyEditor {

	private static final long serialVersionUID = -1012526640443976474L;

	/** 表示タイプ */
	@XmlType(namespace="http://mtp.iplass.org/xml/definition/view/generic")
	public enum DateRangeDisplayType {
		@XmlEnumValue("DateRange")DATERANGE
	}

	/** オブジェクト名 */
	private String objectName;

	/** プロパティエディタ */
	@MetaFieldInfo(
			displayName="プロパティエディタ",
			displayNameKey="generic_editor_DateRangePropertyEditor_editorDisplaNameKey",
			required=true,
			inputType=InputType.REFERENCE,
			fixedReferenceClass={
					DatePropertyEditor.class,
					TimestampPropertyEditor.class,
					TimePropertyEditor.class
					},
			displayOrder=100,
			description="プロパティの型にあわせたプロパティエディタを選択してください。",
			descriptionKey="generic_editor_DateRangePropertyEditor_editorDescriptionKey"
	)
	@EntityViewField()
	private PropertyEditor editor;

	/** FromプロパティでNull入力を許容するか*/
	@MetaFieldInfo(
			displayName="Nullの入力を許可",
			displayNameKey="generic_editor_DateRangePropertyEditor_inputNullFromDisplayNameKey",
			inputType=InputType.CHECKBOX,
			displayOrder=105,
			description="入力値にNullを許可するか設定します。",
			descriptionKey="generic_editor_DateRangePropertyEditor_inputNullFromDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHCONDITION, FieldReferenceType.DETAIL, FieldReferenceType.BULK}
	)
	private boolean inputNullFrom;

	/** Toプロパティ名 */
	@MetaFieldInfo(displayName="Toプロパティ名",
			displayNameKey="generic_editor_DateRangePropertyEditor_toPropertyNameDisplaNameKey",
			required=true,
			inputType=InputType.PROPERTY,
			displayOrder=110,
			description="このプロパティと組み合わせて表示する他のプロパティを指定します。<br>"
					+ "指定するプロパティの型はこのプロパティに合わせて下さい。",
			descriptionKey="generic_editor_DateRangePropertyEditor_toPropertyNameDescriptionKey"
	)
	@EntityViewField()
	private String toPropertyName;

	/** Toプロパティ表示名 */
	@MetaFieldInfo(displayName="詳細検索でのToプロパティ表示名",
			displayNameKey="generic_editor_DateRangePropertyEditor_toPropertyDisplayNameDisplaNameKey",
			required=false,
			inputType=InputType.MULTI_LANG,
			displayOrder=115,
			description="詳細検索で表示するToプロパティのラベルを設定します。",
			descriptionKey="generic_editor_DateRangePropertyEditor_toPropertyDisplayNameDescriptionKey",
			multiLangField="localizedToPropertyDisplayNameList"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHCONDITION}
	)
	@MultiLang()
	private String toPropertyDisplayName;

	/** Toプロパティ表示名の多言語設定情報 */
	@MetaFieldInfo(
			displayName="詳細検索でのToプロパティ表示名の多言語設定",
			displayNameKey="generic_editor_DateRangePropertyEditor_localizedToPropertyDisplayNameListDisplaNameKey",
			inputType=InputType.MULTI_LANG_LIST,
			displayOrder=120
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.DETAIL}
	)
	private List<LocalizedStringDefinition> localizedToPropertyDisplayNameList;

	/** Toプロパティエディタ */
	@MetaFieldInfo(
			displayName="Toプロパティエディタ",
			displayNameKey="generic_editor_DateRangePropertyEditor_toEditorDisplaNameKey",
			inputType=InputType.REFERENCE,
			fixedReferenceClass={
					DatePropertyEditor.class,
					TimestampPropertyEditor.class,
					TimePropertyEditor.class
					},
			displayOrder=125,
			description="プロパティの型にあわせたプロパティエディタを選択してください。<br>"
					+ "未指定の場合、プロパティエディタの設定が有効になります。",
			descriptionKey="generic_editor_DateRangePropertyEditor_toEditorDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHRESULT, FieldReferenceType.DETAIL, FieldReferenceType.BULK}
		)
	private PropertyEditor toEditor;

	/** ToプロパティでNull入力を許容するか*/
	@MetaFieldInfo(
			displayName="Nullの入力を許可",
			displayNameKey="generic_editor_DateRangePropertyEditor_inputNullToDisplayNameKey",
			inputType=InputType.CHECKBOX,
			displayOrder=130,
			description="入力値にNullを許可するか設定します。",
			descriptionKey="generic_editor_DateRangePropertyEditor_inputNullToDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHCONDITION, FieldReferenceType.DETAIL, FieldReferenceType.BULK}
	)
	private boolean inputNullTo;

	/** 同値の入力を許容するか*/
	@MetaFieldInfo(
			displayName="同値の入力を許可",
			displayNameKey="generic_editor_DateRangePropertyEditor_equivalentInputDisplayNameKey",
			inputType=InputType.CHECKBOX,
			displayOrder=135,
			description="入力値に同値を許可するか設定します。",
			descriptionKey="generic_editor_DateRangePropertyEditor_equivalentInputDescriptionKey"
	)
	@EntityViewField(
			referenceTypes={FieldReferenceType.SEARCHCONDITION, FieldReferenceType.DETAIL, FieldReferenceType.BULK}
	)
	private boolean equivalentInput;

	/** エラーメッセージ */
	@MetaFieldInfo(displayName="エラーメッセージ",
			displayNameKey="generic_editor_DateRangePropertyEditor_errorMessageNameDisplaNameKey",
			description="FromとToの大小関係が不正な場合のエラーメッセージを設定します。",
			descriptionKey="generic_editor_DateRangePropertyEditor_errorMessageNameDescriptionKey",
			inputType=InputType.MULTI_LANG,
			multiLangField = "localizedErrorMessageList",
			displayOrder=140
	)
	@EntityViewField()
	@MultiLang()
	private String errorMessage;

	/** エラーメッセージ多言語設定情報 */
	@MetaFieldInfo(
			displayName="多言語設定情報",
			displayNameKey="generic_editor_DateRangePropertyEditor_localizedErrorMessageListDisplaNameKey",
			inputType=InputType.MULTI_LANG_LIST,
			displayOrder=145
	)
	@EntityViewField()
	private List<LocalizedStringDefinition> localizedErrorMessageList;

	@Override
	public DateRangeDisplayType getDisplayType() {
		return DateRangeDisplayType.DATERANGE;
	}

	/**
	 * オブジェクト名を取得します。
	 * @return オブジェクト名
	 */
	public String getObjectName() {
	    return objectName;
	}

	/**
	 * オブジェクト名を設定します。
	 * @param objectName オブジェクト名
	 */
	public void setObjectName(String objectName) {
	    this.objectName = objectName;
	}

	/**
	 * @return editor
	 */
	public PropertyEditor getEditor() {
		return editor;
	}

	/**
	 * @param editor セットする editor
	 */
	public void setEditor(PropertyEditor editor) {
		this.editor = editor;
	}

	/**
	 * @return inputNullFrom
	 */
	public boolean isInputNullFrom() {
		return inputNullFrom;
	}

	/**
	 * @param FromのNull許容フラグをセットする
	 */
	public void setInputNullFrom(boolean inputNullFrom) {
		this.inputNullFrom = inputNullFrom;
	}

	/**
	 * @return toEditor
	 */
	public PropertyEditor getToEditor() {
		return toEditor;
	}

	/**
	 * @param toEditor セットする toEditor
	 */
	public void setToEditor(PropertyEditor toEditor) {
		this.toEditor = toEditor;
	}

	/**
	 * @return toPropertyName
	 */
	public String getToPropertyName() {
		return toPropertyName;
	}

	/**
	 * @param toPropertyName セットする toPropertyName
	 */
	public void setToPropertyName(String toPropertyName) {
		this.toPropertyName = toPropertyName;
	}

	/**
	 * @return toPropertyDisplayName
	 */
	public String getToPropertyDisplayName() {
		return toPropertyDisplayName;
	}

	/**
	 * @param toPropertyDisplayName セットする toPropertyName
	 */
	public void setToPropertyDisplayName(String toPropertyDisplayName) {
		this.toPropertyDisplayName = toPropertyDisplayName;
	}

	/**
	 * Toプロパティ表示名の多言語設定情報を取得します。
	 * @return リスト
	 */
	public List<LocalizedStringDefinition> getLocalizedToPropertyDisplayNameList() {
		return localizedToPropertyDisplayNameList;
	}

	/**
	 * Toプロパティ表示名の多言語設定情報を設定します。
	 * @param localizedDescriptionList リスト
	 */
	public void setLocalizedToPropertyDisplayNameList(List<LocalizedStringDefinition> localizedToPropertyDisplayNameList) {
		this.localizedToPropertyDisplayNameList = localizedToPropertyDisplayNameList;
	}

	/**
	 * @return inputNullTo
	 */
	public boolean isInputNullTo() {
		return inputNullTo;
	}

	/**
	 * @param ToのNull許容フラグをセットする
	 */
	public void setInputNullTo(boolean inputNullTo) {
		this.inputNullTo = inputNullTo;
	}

	/**
	 * @return equivalentInput
	 */
	public boolean isEquivalentInput() {
		return equivalentInput;
	}

	/**
	 * @param 同値の登録の許容フラグをセットする
	 */
	public void setEquivalentInput(boolean equivalentInput) {
		this.equivalentInput = equivalentInput;
	}


	/**
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage セットする errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return localizedErrorMessageList
	 */
	public List<LocalizedStringDefinition> getLocalizedErrorMessageList() {
		return localizedErrorMessageList;
	}

	/**
	 * @param localizedErrorMessageList セットする localizedErrorMessageList
	 */
	public void setLocalizedErrorMessageList(List<LocalizedStringDefinition> localizedErrorMessageList) {
		this.localizedErrorMessageList = localizedErrorMessageList;
	}
}
