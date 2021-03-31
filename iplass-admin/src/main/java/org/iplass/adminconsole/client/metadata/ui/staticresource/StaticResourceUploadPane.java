/*
 * Copyright (C) 2016 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

package org.iplass.adminconsole.client.metadata.ui.staticresource;

import java.util.LinkedHashMap;

import org.iplass.adminconsole.client.base.i18n.AdminClientMessageUtil;
import org.iplass.adminconsole.client.base.io.download.GetRequestBuilder;
import org.iplass.adminconsole.client.base.io.download.PostDownloadFrame;
import org.iplass.adminconsole.client.base.io.upload.UploadFileItem;
import org.iplass.adminconsole.client.base.tenant.TenantInfoHolder;
import org.iplass.adminconsole.client.base.util.SmartGWTUtil;
import org.iplass.adminconsole.shared.metadata.dto.staticresource.FileType;
import org.iplass.adminconsole.shared.metadata.dto.staticresource.LocalizedStaticResourceInfo;
import org.iplass.adminconsole.shared.metadata.dto.staticresource.StaticResourceDownloadProperty;
import org.iplass.adminconsole.shared.metadata.dto.staticresource.StaticResourceInfo;
import org.iplass.mtp.web.actionmapping.definition.result.ContentDispositionType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FileUpload;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.events.ClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class StaticResourceUploadPane extends VLayout {

	/** Fileの種類選択用Map */
	private static LinkedHashMap<String, String> fileTypeMap;
	static {
		fileTypeMap = new LinkedHashMap<String, String>();
		for (FileType type : FileType.values()) {
			fileTypeMap.put(type.name(), type.displayName());
		}
	}

	/** ファイルタイプ */
	private DynamicForm fileTypePane;
	private SelectItem fileTypeField;

	/** バイナリファイル */
	private UploadFileItem itmFileUpload;

	/** 保存済みテンプレートファイルダウンロード */
	private DynamicForm downloadForm;
	private ButtonItem downloadFilebtn;
	private StaticTextItem txtDownloadFileName;

	private Label lblRegistedStatus;
	private Img imgRegistedBinary;

	public StaticResourceUploadPane() {
		// レイアウト設定
		setHeight(300);
		setWidth100();
		setMargin(5);
		setMembersMargin(5);

		fileTypeField = new SelectItem("fileType", "File Type");
		fileTypeField.setValueMap(fileTypeMap);

		fileTypePane = new DynamicForm();
		fileTypePane.setItems(fileTypeField);
		SmartGWTUtil.setRequired(fileTypeField);

		itmFileUpload = new UploadFileItem();

		downloadForm = new DynamicForm();
		downloadForm.setWidth100();
		downloadForm.setMargin(5);

		txtDownloadFileName = new StaticTextItem();
		txtDownloadFileName.setTitle("Saved File");

		downloadFilebtn = new ButtonItem("Download", "Download");
		downloadFilebtn.setWidth(100);
		downloadFilebtn.setStartRow(false);
		downloadForm.setItems(txtDownloadFileName, new SpacerItem(), downloadFilebtn);

		HLayout previewComposit = new HLayout(5);
		previewComposit.setHeight100();

		Label previewLabel = new Label("Saved File Preview :");
		previewLabel.setStyleName("formTitle");
		previewLabel.setWrap(false);
		previewLabel.setAlign(Alignment.RIGHT);
		previewComposit.addMember(previewLabel);

		VLayout previewContents = new VLayout();
		previewContents.setWidth100();

		lblRegistedStatus = new Label();
		lblRegistedStatus.setHeight(25);
		previewContents.addMember(lblRegistedStatus);

		imgRegistedBinary = new Img();
		imgRegistedBinary.setPadding(5);
		imgRegistedBinary.setWidth100();
		imgRegistedBinary.setHeight100();
		imgRegistedBinary.setOverflow(Overflow.AUTO);
		imgRegistedBinary.setBorder("1px solid gray");
		imgRegistedBinary.setImageType(ImageStyle.NORMAL);
		previewContents.addMember(imgRegistedBinary);

		Label previewComment = new Label(AdminClientMessageUtil.getString("ui_metadata_staticresource_StaticResourceUploadPane_previewComment"));
		previewComment.setHeight(25);
		previewComment.setWrap(false);
		previewContents.addMember(previewComment);
		previewComposit.addMember(previewContents);

		// 配置
		addMember(fileTypePane);
		addMember(itmFileUpload);
		addMember(downloadForm);
		addMember(previewComposit);
	}

	public void setDefinition(StaticResourceInfo definition) {

		if (definition.getFileType() != null) {
			this.fileTypeField.setValue(definition.getFileType());
		} else {
			this.fileTypeField.setValue(FileType.SIMPLE);
		}

		//選択ファイルをクリア
		this.itmFileUpload.clearFile();

		// Previewの設定
		setupImagePreview(definition);
	}

	public void setDefinition(LocalizedStaticResourceInfo definition, UploadFileItem fileItem) {
		if (definition.getFileType() != null) {
			this.fileTypeField.setValue(definition.getFileType());
		} else {
			this.fileTypeField.setValue(FileType.SIMPLE);
		}

		if (fileItem != null) {
			this.itmFileUpload.setFileUpload(fileItem.getEditFileUpload());
		}

		// Previewの設定
		setupImagePreview(definition);
	}

	public StaticResourceInfo getEditStaticResourceDefinition(StaticResourceInfo definition) {
		String fileTypeStr = SmartGWTUtil.getStringValue(this.fileTypeField);
		definition.setFileType(fileTypeStr != null && !fileTypeStr.isEmpty() ? FileType.valueOf(fileTypeStr) : null);

		return definition;
	}

	public LocalizedStaticResourceInfo getEditLocalizedStaticResourceDefinition(LocalizedStaticResourceInfo definition) {
		String fileTypeStr = SmartGWTUtil.getStringValue(this.fileTypeField);
		definition.setFileType(fileTypeStr != null && !fileTypeStr.isEmpty() ? FileType.valueOf(fileTypeStr) : null);

		return definition;
	}

	public UploadFileItem getEditUploadFileItem() {
		return this.itmFileUpload;
	}

	public FileUpload getEditFileUpload() {
		return this.itmFileUpload.getEditFileUpload();
	}

	public void resetSrcImg() {
		this.imgRegistedBinary.resetSrc();
	}

	public boolean validate() {
		return this.fileTypePane.validate();
	}

	private void setupImagePreview(StaticResourceInfo definition) {
		setupImagePreview(definition.getFileType(), definition.getName(), definition.getStoredBinaryName(), null);
	}

	private void setupImagePreview(LocalizedStaticResourceInfo definition) {
		setupImagePreview(definition.getFileType(), definition.getName(), definition.getStoredBinaryName(), definition.getLocaleName());
	}

	private void setupImagePreview(FileType fileType, String defName, String binaryName, String lang) {
		if (!SmartGWTUtil.isEmpty(binaryName)) {
			txtDownloadFileName.setValue(binaryName);
			setTemplateDownloadAction(defName, lang);
			downloadForm.setVisible(true);

			GetRequestBuilder builder = null;
			if (fileType == FileType.SIMPLE) {
				builder = new GetRequestBuilder();
				builder.baseUrl(GWT.getModuleBaseURL());
				builder.targetUrl(StaticResourceDownloadProperty.ACTION_URL);
				builder.parameterWithValue(StaticResourceDownloadProperty.TENANT_ID, String.valueOf(TenantInfoHolder.getId()));
				builder.parameterWithValue(StaticResourceDownloadProperty.DEFINITION_NAME, defName);
				builder.parameterWithValue(StaticResourceDownloadProperty.CONTENT_DISPOSITION_TYPE, ContentDispositionType.INLINE.name());
				if (!SmartGWTUtil.isEmpty(lang)) {
					builder.parameterWithValue(StaticResourceDownloadProperty.LANG, lang);
				}
				builder.parameterWithValue("dummy", String.valueOf(System.currentTimeMillis()));	// 画像変換時の再描画のため
			}
			imgRegistedBinary.setSrc(builder != null ? builder.toEncodedUrl() : null);
			imgRegistedBinary.setVisible(builder != null);
			lblRegistedStatus.setContents("Resource data is registered.");
		} else {
			downloadForm.setVisible(false);

			imgRegistedBinary.setSrc((String)null);
			imgRegistedBinary.setVisible(false);
			lblRegistedStatus.setContents("Still no resource data.");
		}
	}

	private void setTemplateDownloadAction(final String defName, final String lang) {

		com.smartgwt.client.widgets.form.fields.events.ClickHandler handler =
			new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					PostDownloadFrame frame = new PostDownloadFrame();
					frame.setAction(GWT.getModuleBaseURL() + StaticResourceDownloadProperty.ACTION_URL)
						.addParameter(StaticResourceDownloadProperty.TENANT_ID, String.valueOf(TenantInfoHolder.getId()))
						.addParameter(StaticResourceDownloadProperty.DEFINITION_NAME, defName)
						.addParameter(StaticResourceDownloadProperty.CONTENT_DISPOSITION_TYPE, ContentDispositionType.ATTACHMENT.name())
						.addParameter("dummy", String.valueOf(System.currentTimeMillis()));
					if (!SmartGWTUtil.isEmpty(lang)) {
						frame.addParameter(StaticResourceDownloadProperty.LANG, lang);
					}
					frame.execute();
				}
			};
		downloadFilebtn.addClickHandler(handler);
	}

}
