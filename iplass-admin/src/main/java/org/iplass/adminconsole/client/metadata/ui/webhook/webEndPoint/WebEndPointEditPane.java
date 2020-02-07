/*
 * Copyright (C) 2020 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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
package org.iplass.adminconsole.client.metadata.ui.webhook.webEndPoint;

import java.io.Serializable;
import java.util.Map;

import org.iplass.adminconsole.client.base.event.DataChangedEvent;
import org.iplass.adminconsole.client.base.event.DataChangedHandler;
import org.iplass.adminconsole.client.base.i18n.AdminClientMessageUtil;
import org.iplass.adminconsole.client.base.rpc.AdminAsyncCallback;
import org.iplass.adminconsole.client.base.tenant.TenantInfoHolder;
import org.iplass.adminconsole.client.base.ui.widget.form.MtpTextItem;
import org.iplass.adminconsole.client.base.util.SmartGWTUtil;
import org.iplass.adminconsole.client.metadata.ui.DefaultMetaDataPlugin;
import org.iplass.adminconsole.client.metadata.ui.MetaDataItemMenuTreeNode;
import org.iplass.adminconsole.client.metadata.ui.MetaDataMainEditPane;
import org.iplass.adminconsole.client.metadata.ui.common.MetaCommonAttributeSection;
import org.iplass.adminconsole.client.metadata.ui.common.MetaCommonHeaderPane;
import org.iplass.adminconsole.client.metadata.ui.common.MetaDataHistoryDialog;
import org.iplass.adminconsole.client.metadata.ui.common.MetaDataUpdateCallback;
import org.iplass.adminconsole.client.metadata.ui.common.StatusCheckUtil;
import org.iplass.adminconsole.shared.metadata.dto.AdminDefinitionModifyResult;
import org.iplass.adminconsole.shared.metadata.rpc.MetaDataServiceAsync;
import org.iplass.adminconsole.shared.metadata.rpc.MetaDataServiceFactory;
import org.iplass.mtp.definition.DefinitionEntry;
import org.iplass.mtp.webhook.template.endpointaddress.WebEndPointDefinition;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

public class WebEndPointEditPane extends MetaDataMainEditPane {
	
	private final MetaDataServiceAsync service;
	/** 編集対象 */
	private WebEndPointDefinition curDefinition;
	private int curVersion;
	private String curDefinitionId;
	private MetaCommonHeaderPane headerPane;
	
	/** 共通属性 */
	private MetaCommonAttributeSection<WebEndPointDefinition> commonSection;
	
	/** 個別属性 */
	private WebEndPointAttributePane webEndPointAttributePane;
	
	public WebEndPointEditPane(MetaDataItemMenuTreeNode targetNode, DefaultMetaDataPlugin plugin) {
		super(targetNode, plugin);
		service = MetaDataServiceFactory.get();
		//レイアウト設定
		setWidth100();
		
		//ヘッダ（ボタン）部分
		headerPane = new MetaCommonHeaderPane(targetNode);
		headerPane.setSaveClickHandler(new SaveClickHandler());
		headerPane.setCancelClickHandler(new CancelClickHandler());

		headerPane.setHistoryClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MetaDataHistoryDialog metaDataHistoryDialog = new MetaDataHistoryDialog(curDefinition.getClass().getName(), curDefinitionId, curVersion);
				metaDataHistoryDialog.show();
			}
		});
		commonSection = new MetaCommonAttributeSection<>(targetNode, WebEndPointDefinition.class);
		
		webEndPointAttributePane = new WebEndPointAttributePane();
		SectionStackSection webEndPointAttributeSection = createSection("Web End Point Addresses", webEndPointAttributePane);
		
		setMainSections(commonSection,webEndPointAttributeSection);
		addMember(headerPane);
		addMember(mainStack);
		
		initializeData();
	}
	/**
	 * データ初期化処理
	 */
	private void initializeData() {
//		エラーのクリア
		commonSection.clearErrors();
		webEndPointAttributePane.clearErrors();

		service.getDefinitionEntry(TenantInfoHolder.getId(), WebEndPointDefinition.class.getName(), defName, new AsyncCallback<DefinitionEntry>() {

			@Override
			public void onFailure(Throwable caught) {
				SC.say(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_failed"),
						AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_failedGetScreenInfo"));

				GWT.log(caught.toString(), caught);
			}

			@Override
			public void onSuccess(DefinitionEntry result) {

				//画面に反映
				setDefinition(result);
			}

		});

//		ステータスチェック
		StatusCheckUtil.statuCheck(WebEndPointDefinition.class.getName(), defName, this);
	}
	
	/**
	 * Definition画面設定内容入り
	 *
	 * @param definition 編集対象
	 */
	protected void setDefinition(DefinitionEntry entry) {
		
		this.curDefinition = (WebEndPointDefinition) entry.getDefinition();
		this.curVersion = entry.getDefinitionInfo().getVersion();
		this.curDefinitionId = entry.getDefinitionInfo().getObjDefId();
		
		commonSection.setDefinition(curDefinition);
		webEndPointAttributePane.setDefinition(curDefinition);
		
	}

	/**
	 * 更新処理
	 *
	 * @param definition 更新対象
	 */
	private void updateWebEndPointDefinition(final WebEndPointDefinition definition, boolean checkVersion) {
		SmartGWTUtil.showSaveProgress();
		service.updateDefinition(TenantInfoHolder.getId(), definition, curVersion, checkVersion, new MetaDataUpdateCallback() {

			@Override
			protected void overwriteUpdate() {
				updateWebEndPointDefinition(definition, false);
			}

			@Override
			protected void afterUpdate(AdminDefinitionModifyResult result) {
				updateComplete(definition);
			}
		});
	}
	
	/**
	 * 更新完了処理
	 *
	 * @param definition 更新対象
	 */
	private void updateComplete(WebEndPointDefinition definition) {
		SC.say(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_completion"),
				AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_saveWebHookTemplate"));

		//再表示
		initializeData();
		commonSection.refreshSharedConfig();

		//ツリー再表示
		plugin.refreshWithSelect(definition.getName(), new AsyncCallback<MetaDataItemMenuTreeNode>() {
			@Override
			public void onSuccess(MetaDataItemMenuTreeNode result) {
				headerPane.setTargetNode(result);
				commonSection.setTargetNode(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	
	/**
	 * 保存ボタンイベント
	 */
	private final class SaveClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			boolean commonValidate = commonSection.validate();
			boolean webEndPointValidate = webEndPointAttributePane.validate();
			if (!commonValidate || !webEndPointValidate) {
				return;
			}
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_webhook_webEndPointEditPane_saveConfirm"),
					AdminClientMessageUtil.getString("ui_metadata_webhook_webEndPointEditPane_saveWebHookTemplateComment"), new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					if (value) {
						WebEndPointDefinition definition = curDefinition;
						definition = commonSection.getEditDefinition(definition);
						definition = webEndPointAttributePane.getEditDefinition(definition);

						updateWebEndPointDefinition(definition, true);
					}
				}
			});
		}
	}

	/**
	 * キャンセルボタンイベント
	 */
	private final class CancelClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_cancelConfirm"),
					AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_cancelConfirmComment")
					, new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					if (value) {
						initializeData();
						commonSection.refreshSharedConfig();
					}
				}
			});
		}
	}
	
	
	private class WebEndPointAttributePane extends VLayout {
		
		private DynamicForm urlForm;
		private DynamicForm headerAuthForm;
		private DynamicForm hmacForm;
		
		private TextAreaItem webEndPointUrlField;
		
		//basic authentication
		private TextItem webEndPointBasicUsernameField;
		private TextItem webEndPointBasicPasswordField;

		
		//token authentication
		private TextAreaItem webEndPointHmacTokenField;
		private TextAreaItem webEndPointBearerTokenField;

		public WebEndPointAttributePane() {
			setOverflow(Overflow.AUTO);
			VLayout mainPane = new VLayout();
			mainPane.setMargin(5);
			mainPane.setMembersMargin(5);

			urlForm =new DynamicForm();
			urlForm.setWidth("100%");
			urlForm.setPadding(10);
			urlForm.setNumCols(2);
			urlForm.setColWidths(100,"*");
			urlForm.setIsGroup(true);
			urlForm.setGroupTitle("EndPoint Address");
			webEndPointUrlField = new TextAreaItem("subscriberurl", "EndPoint URL Template");
			webEndPointUrlField.setWidth("100%");
			webEndPointUrlField.setHeight(200);
			webEndPointUrlField.setBrowserSpellCheck(false);
			webEndPointUrlField.setPrompt(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_webEndPointUrlFieldHoverInfo"));
			SmartGWTUtil.setRequired(webEndPointUrlField);
			urlForm.setItems(webEndPointUrlField);
			
			VLayout authPane = new VLayout();
			authPane.setMargin(5);
			authPane.setMembersMargin(5);

			//TODO:現在はBasic, Bearerだけ、今後追加かと
			//authorizationでよく使うタイプ: Basic, Bearer, Digest, HOBA, Mutual, Client, Form Basedとか、
			headerAuthForm =new DynamicForm();
			headerAuthForm.setWidth("100%");
			headerAuthForm.setPadding(10);
			headerAuthForm.setNumCols(2);
			headerAuthForm.setColWidths(100,"*");
			headerAuthForm.setIsGroup(true);
			headerAuthForm.setGroupTitle("Header Authentication");
			
			webEndPointBasicUsernameField = new MtpTextItem("basicusername","Basic Username");
			webEndPointBasicUsernameField.setCanEdit(false);
			webEndPointBasicUsernameField.setCanFocus(false);
			webEndPointBasicUsernameField.setTextBoxStyle("textItemDisabled");
			
			webEndPointBasicPasswordField = new MtpTextItem("basicpassword","Basic Password");
			webEndPointBasicPasswordField.setCanEdit(false);
			webEndPointBasicPasswordField.setCanFocus(false);
			webEndPointBasicPasswordField.setTextBoxStyle("textItemDisabled");

			webEndPointBearerTokenField = new TextAreaItem("subscribersecuritybearertoken","Bearer Token");
			webEndPointBearerTokenField.setWidth("*");
			webEndPointBearerTokenField.setHeight(300);
			webEndPointBearerTokenField.setCanEdit(false);
			webEndPointBearerTokenField.setCanFocus(false);
			webEndPointBearerTokenField.setTextBoxStyle("textItemDisabled");
			ButtonItem editHeaderAuthButton = new ButtonItem(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_editHeaderAuthButton"));
			editHeaderAuthButton.setWidth(150);
			editHeaderAuthButton.setPrompt(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_headerAuthFormHoverInfo"));
			editHeaderAuthButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					if (curDefinition.getHeaderAuthType()==null || curDefinition.getHeaderAuthType().isEmpty()) {
						editMap(null, null);
						return;
					}
					service.getWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), curDefinition.getHeaderAuthType(), new AdminAsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							editMap(curDefinition.getHeaderAuthType(), result);
						}

						@Override
						protected void beforeFailure(Throwable caught){
							SmartGWTUtil.hideProgress();
						};
					});
				}
			});
			headerAuthForm.setItems(webEndPointBasicUsernameField,webEndPointBasicPasswordField, webEndPointBearerTokenField, editHeaderAuthButton);

			hmacForm =new DynamicForm();
			hmacForm.setWidth("100%");
			hmacForm.setPadding(10);
			hmacForm.setNumCols(2);
			hmacForm.setColWidths(100,"*");
			hmacForm.setIsGroup(true);
			hmacForm.setGroupTitle("Hmac Token Authentication");
			webEndPointHmacTokenField = new TextAreaItem("securityhmactoken","HMAC Token");
			webEndPointHmacTokenField.setWidth("*");
			webEndPointHmacTokenField.setHeight(300);
			SmartGWTUtil.setReadOnlyTextArea(webEndPointHmacTokenField);
			
			ButtonItem generateHmacButton = new ButtonItem(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_generateHmacButton"));
			generateHmacButton.setWidth(150);
			generateHmacButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					generateHmac();
				}
			});
			ButtonItem editHmacButton = new ButtonItem(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_editHmacButton"));
			editHmacButton.setWidth(150);
			editHmacButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
				@Override
				public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
					service.getWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), "WHHM", new AdminAsyncCallback<String>() {

						@Override
						public void onSuccess(String result) {
							editMap("WHHM", result);
						}

						@Override
						protected void beforeFailure(Throwable caught){
							SmartGWTUtil.hideProgress();
						};
					});
				}
			});
			generateHmacButton.setPrompt(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_HmacButtonHoverInfo"));
			editHmacButton.setPrompt(AdminClientMessageUtil.getString("ui_metadata_webhook_WebEndPointEditPane_HmacButtonHoverInfo"));
			
			hmacForm.setItems(webEndPointHmacTokenField,generateHmacButton,editHmacButton);


			
			authPane.addMember(headerAuthForm);
			authPane.addMember(hmacForm);
			
			mainPane.addMember(urlForm);
			mainPane.addMember(authPane);
			
			addMember(mainPane);
		}
		protected void generateHmac() {
			service.generateHmacTokenString(new AdminAsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					editMap("WHHM", result);
				}

				@Override
				protected void beforeFailure(Throwable caught){
					SmartGWTUtil.hideProgress();
				};
			});
		}
		protected void editMap(String tokenType, String tokenContent) {
			final WebEndPointSecurityInfoEditDialog dialog = new WebEndPointSecurityInfoEditDialog(tokenType,tokenContent);
			
			dialog.addDataChangeHandler(new DataChangedHandler() {
				@Override
				public void onDataChanged(DataChangedEvent event) {
					curDefinition = getEditDefinition(curDefinition);
					Map<String, Serializable> valueMap = event.getValueMap();
					String type =(String) valueMap.get("type");
					String content =(String) valueMap.get("content");
					if (type == null || type.isEmpty()) {

					} else if (type.equals("WHHM")) {
						service.updateWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), content, type,new AdminAsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								setDefinition(curDefinition);
								webEndPointAttributePane.markForRedraw();
								dialog.destroy();
								SmartGWTUtil.hideProgress();
							}

							@Override
							protected void beforeFailure(Throwable caught){
								SmartGWTUtil.hideProgress();
							};
						});
					} else {
						curDefinition.setHeaderAuthType(type);
						service.updateWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), content, type,new AdminAsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								setDefinition(curDefinition);
								webEndPointAttributePane.markForRedraw();
								dialog.destroy();
								SmartGWTUtil.hideProgress();
							}

							@Override
							protected void beforeFailure(Throwable caught){
								SmartGWTUtil.hideProgress();
							};
						});
					}
					webEndPointAttributePane.markForRedraw();
				}
			});
			dialog.show();
		}
		
		public void setHeaderAuthTokenVisibility() {
			if (curDefinition.getHeaderAuthType()!=null) {
				if (curDefinition.getHeaderAuthType().equals("WHBA")){
					webEndPointBearerTokenField.setVisible(false);
					webEndPointBasicPasswordField.setVisible(true);
					webEndPointBasicUsernameField.setVisible(true);
				}
				if (curDefinition.getHeaderAuthType().equals("WHBT")){
					webEndPointBearerTokenField.setVisible(true);
					webEndPointBasicPasswordField.setVisible(false);
					webEndPointBasicUsernameField.setVisible(false);
				}
			} else {
				webEndPointBearerTokenField.setVisible(true);
				webEndPointBasicPasswordField.setVisible(true);
				webEndPointBasicUsernameField.setVisible(true);
			}
			webEndPointAttributePane.markForRedraw();
		}
		/** definition -> pane */
		public void setDefinition(WebEndPointDefinition definition) {
			if (definition != null) {
				webEndPointUrlField.setValue(curDefinition.getUrl());
				service.getWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), "WHHM", new AdminAsyncCallback<String>() {
					@Override
					public void onSuccess(String result) {
						if (result==null||result.isEmpty()) {
							webEndPointHmacTokenField.clearValue();
						}
						else {
							webEndPointHmacTokenField.setValue(result);
						}
					}
					@Override
					protected void beforeFailure(Throwable caught){
						SmartGWTUtil.hideProgress();
					};
				});
				if (curDefinition.getHeaderAuthType()!=null||curDefinition.getHeaderAuthType().isEmpty()) {
					if (curDefinition.getHeaderAuthType().equals("WHBA")) {
						service.getWebEndPointSecurityInfo(TenantInfoHolder.getId(), curDefinition.getWebEndPointId(), "WHBA", new AdminAsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								if (result==null||result.isEmpty()) {
									webEndPointBasicUsernameField.clearValue();
									webEndPointBasicPasswordField.clearValue();
								}
								else {
									String[] basics = result.split(":");
									if (basics.length < 2) {
										webEndPointBasicUsernameField.clearValue();
										webEndPointBasicPasswordField.clearValue();
									} else {
										webEndPointBasicUsernameField.setValue(basics[0]);
										webEndPointBasicPasswordField.setValue(basics[1]);
									}
								}
							}
							@Override
							protected void beforeFailure(Throwable caught){
								SmartGWTUtil.hideProgress();
							};
						});
					} else if (curDefinition.getHeaderAuthType().equals("WHBT")) {
						service.getWebEndPointSecurityInfo(TenantInfoHolder.getId(),curDefinition.getWebEndPointId(), "WHBT", new AdminAsyncCallback<String>() {
							@Override
							public void onSuccess(String result) {
								if (result==null||result.isEmpty()) {
									webEndPointBearerTokenField.clearValue();
								}
								else {
									webEndPointBearerTokenField.setValue(result);
								}
							}
							@Override
							protected void beforeFailure(Throwable caught){
								SmartGWTUtil.hideProgress();
							};
						});
					}
				}
			} else {
				webEndPointUrlField.clearValue();
				webEndPointBasicUsernameField.clearValue();
				webEndPointBasicPasswordField.clearValue();
				webEndPointHmacTokenField.clearValue();
				webEndPointBearerTokenField.clearValue();
			}
			setHeaderAuthTokenVisibility();
		}

		/** pane -> definition */
		public WebEndPointDefinition getEditDefinition(WebEndPointDefinition definition) {
			definition.setUrl(SmartGWTUtil.getStringValue(webEndPointUrlField));
			return definition;
		}
		
		public boolean validate() {
			return true;
		}

		public void clearErrors() {
//			urlForm.clearErrors(true);
//			headerAuthForm.clearErrors(true);
//			hmacForm.clearErrors(true);
		}
	}
}
