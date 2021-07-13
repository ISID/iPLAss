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

package org.iplass.mtp.impl.view.generic.editor;

import java.util.ArrayList;
import java.util.List;

import org.iplass.mtp.entity.definition.PropertyDefinition;
import org.iplass.mtp.impl.entity.EntityContext;
import org.iplass.mtp.impl.entity.EntityHandler;
import org.iplass.mtp.impl.i18n.I18nUtil;
import org.iplass.mtp.impl.i18n.MetaLocalizedString;
import org.iplass.mtp.impl.metadata.MetaDataIllegalStateException;
import org.iplass.mtp.impl.metadata.MetaDataRuntime;
import org.iplass.mtp.impl.util.ObjectUtil;
import org.iplass.mtp.impl.view.generic.EntityViewRuntime;
import org.iplass.mtp.impl.view.generic.FormViewRuntime;
import org.iplass.mtp.impl.view.generic.HasEntityProperty;
import org.iplass.mtp.impl.view.generic.element.property.MetaPropertyLayout;
import org.iplass.mtp.view.generic.editor.NumericRangePropertyEditor;
import org.iplass.mtp.view.generic.editor.PropertyEditor;

/**
 * 数値範囲型プロパティエディタのメタデータ
 * @author ICOM Shojima
 */
public class MetaNumericRangePropertyEditor extends MetaCustomPropertyEditor implements HasEntityProperty {

	/** SerialVersionUID */
	private static final long serialVersionUID = 7792744945686490609L;

	public static MetaNumericRangePropertyEditor createInstance(PropertyEditor editor) {
		return new MetaNumericRangePropertyEditor();
	}

	/** オブジェクトID */
	private String objectId;

	/** プロパティエディタ */
	private MetaPropertyEditor editor;

	/** FromのNull許容フラグ */
	private boolean inputNullFrom;

	/** Toプロパティエディタ */
	private MetaPropertyEditor toEditor;

	/** ToプロパティID */
	private String toPropertyId;

	/** Toプロパティ表示名 */
	private String toPropertyDisplayName;

	/** Toプロパティ表示名の多言語設定情報 */
	private List<MetaLocalizedString> localizedToPropertyDisplayNameList = new ArrayList<MetaLocalizedString>();

	/** ToのNull許容フラグ */
	private boolean inputNullTo;

	/** 同値許容フラグ */
	private boolean equivalentInput;

	/** エラーメッセージ */
	private String errorMessage;

	/** エラーメッセージ多言語設定情報 */
	private List<MetaLocalizedString> localizedErrorMessageList = new ArrayList<>();

	/**
	 * オブジェクトIDを取得します。
	 * @return オブジェクトID
	 */
	public String getObjectId() {
	    return objectId;
	}

	/**
	 * オブジェクトIDを設定します。
	 * @param objectId オブジェクトID
	 */
	public void setObjectId(String objectId) {
	    this.objectId = objectId;
	}

	/**
	 * プロパティエディタを取得します。
	 * @return プロパティエディタ
	 */
	public MetaPropertyEditor getEditor() {
	    return editor;
	}

	/**
	 * プロパティエディタを設定します。
	 * @param editor プロパティエディタ
	 */
	public void setEditor(MetaPropertyEditor editor) {
	    this.editor = editor;
	}

	/**
	 *  FromのNull許容フラグを取得します。
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
	 * Toプロパティエディタを取得します。
	 * @return Toプロパティエディタ
	 */
	public MetaPropertyEditor getToEditor() {
	    return toEditor;
	}

	/**
	 * Toプロパティエディタを設定します。
	 * @param toEditor Toプロパティエディタ
	 */
	public void setToEditor(MetaPropertyEditor toEditor) {
	    this.toEditor = toEditor;
	}

	/**
	 * @return toPropertyId
	 */
	public String getToPropertyId() {
		return toPropertyId;
	}

	/**
	 * @param toPropertyId セットする toPropertyId
	 */
	public void setToPropertyId(String toPropertyId) {
		this.toPropertyId = toPropertyId;
	}

	/**
	 * @return toPropertyDisplayName
	 */
	public String getToPropertyDisplayName() {
		return toPropertyDisplayName;
	}

	/**
	 * @param toPropertyDisplayName セットする toPropertyDisplayName
	 */
	public void setToPropertyDisplayName(String toPropertyDisplayName) {
		this.toPropertyDisplayName = toPropertyDisplayName;
	}

	/**
	 * Toプロパティ表示名の多言語設定情報を取得します。
	 * @return リスト
	 */
	public List<MetaLocalizedString> getLocalizedToPropertyDisplayNameList() {
		return localizedToPropertyDisplayNameList;
	}

	/**
	 * Toプロパティ表示名の多言語設定情報を設定します。
	 * @param localizedDescriptionList リスト
	 */
	public void setLocalizedToPropertyDisplayNameList(List<MetaLocalizedString> localizedToPropertyDisplayNameList) {
		this.localizedToPropertyDisplayNameList = localizedToPropertyDisplayNameList;
	}

	/**
	 * @return inputNullFrom
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
	public List<MetaLocalizedString> getLocalizedErrorMessageList() {
		return localizedErrorMessageList;
	}

	/**
	 * @param localizedErrorMessageList セットする localizedErrorMessageList
	 */
	public void setLocalizedErrorMessageList(List<MetaLocalizedString> localizedErrorMessageList) {
		this.localizedErrorMessageList = localizedErrorMessageList;
	}

	@Override
	public void applyConfig(PropertyEditor _editor) {
		super.fillFrom(_editor);

		NumericRangePropertyEditor e = (NumericRangePropertyEditor) _editor;

		EntityContext metaContext = EntityContext.getCurrentContext();
		EntityHandler entity = metaContext.getHandlerByName(e.getObjectName());

		objectId = entity.getMetaData().getId();
		if (e.getEditor() != null) {
			editor = MetaPropertyEditor.createInstance(e.getEditor());
			editor.applyConfig(e.getEditor());
		}
		if (e.getToEditor() != null) {
			toEditor = MetaPropertyEditor.createInstance(e.getEditor());
			toEditor.applyConfig(e.getToEditor());
		}

		setInputNullFrom(e.isInputNullFrom());
		setInputNullTo(e.isInputNullTo());
		setEquivalentInput(e.isEquivalentInput());

		toPropertyId = convertId(e.getToPropertyName(), metaContext, entity);
		setToPropertyDisplayName(e.getToPropertyDisplayName());

		localizedToPropertyDisplayNameList = I18nUtil.toMeta(e.getLocalizedToPropertyDisplayNameList());

		if (toPropertyId == null) {
			throw new MetaDataIllegalStateException("to property name is not defined.");
		}

		errorMessage = e.getErrorMessage();
		if (e.getLocalizedErrorMessageList() != null && !e.getLocalizedErrorMessageList().isEmpty()) {
			localizedErrorMessageList = I18nUtil.toMeta(e.getLocalizedErrorMessageList());
		}
	}

	@Override
	public PropertyEditor currentConfig(String propertyName) {
		//対象Entityの存在チェック
		EntityContext metaContext = EntityContext.getCurrentContext();
		EntityHandler entity = metaContext.getHandlerById(objectId);
		if (entity == null) {
			return null;
		}
		NumericRangePropertyEditor _editor = new NumericRangePropertyEditor();
		super.fillTo(_editor);

		_editor.setObjectName(entity.getMetaData().getName());
		if (editor != null) {
			_editor.setEditor(editor.currentConfig(propertyName));
		}
		if (toEditor != null) {
			_editor.setToEditor(toEditor.currentConfig(propertyName));
		}

		if (toPropertyId != null) {
			_editor.setToPropertyName(convertName(toPropertyId, metaContext, entity));
			_editor.setToPropertyDisplayName(toPropertyDisplayName);
		}

		_editor.setLocalizedToPropertyDisplayNameList(I18nUtil.toDef(localizedToPropertyDisplayNameList));
		_editor.setInputNullFrom(inputNullFrom);
		_editor.setInputNullTo(inputNullTo);
		_editor.setEquivalentInput(equivalentInput);

		_editor.setErrorMessage(errorMessage);
		if (localizedErrorMessageList != null && !localizedErrorMessageList.isEmpty()) {
			_editor.setLocalizedErrorMessageList(I18nUtil.toDef(localizedErrorMessageList));
		}

		return _editor;
	}

	@Override
	public MetaNumericRangePropertyEditor copy() {
		return ObjectUtil.deepCopy(this);
	}

	@Override
	public MetaDataRuntime createRuntime(EntityViewRuntime entityView, FormViewRuntime formView,
			MetaPropertyLayout propertyLayout, EntityContext context, EntityHandler eh) {
		return new PropertyEditorRuntime(entityView, formView, propertyLayout, context, eh) {
			@Override
			protected boolean checkPropertyType(PropertyDefinition pd) {
				return true;
			}
		};
	}

}
