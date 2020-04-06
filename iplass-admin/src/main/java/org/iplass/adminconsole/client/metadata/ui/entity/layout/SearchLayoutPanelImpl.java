/*
 * Copyright (C) 2011 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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

package org.iplass.adminconsole.client.metadata.ui.entity.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iplass.adminconsole.client.base.event.MTPEvent;
import org.iplass.adminconsole.client.base.event.MTPEventHandler;
import org.iplass.adminconsole.client.base.i18n.AdminClientMessageUtil;
import org.iplass.adminconsole.client.base.rpc.AdminAsyncCallback;
import org.iplass.adminconsole.client.base.tenant.TenantInfoHolder;
import org.iplass.adminconsole.client.base.util.SmartGWTUtil;
import org.iplass.adminconsole.client.metadata.data.entity.layout.ViewType;
import org.iplass.adminconsole.client.metadata.ui.MetaDataItemMenuTreeNode;
import org.iplass.adminconsole.client.metadata.ui.MetaDataMainEditPane;
import org.iplass.adminconsole.client.metadata.ui.common.MetaCommonAttributeSection;
import org.iplass.adminconsole.client.metadata.ui.common.MetaCommonHeaderPane;
import org.iplass.adminconsole.client.metadata.ui.common.MetaDataHistoryDialog;
import org.iplass.adminconsole.client.metadata.ui.common.MetaDataUpdateCallback;
import org.iplass.adminconsole.client.metadata.ui.common.StatusCheckUtil;
import org.iplass.adminconsole.client.metadata.ui.entity.EntityPlugin;
import org.iplass.adminconsole.client.metadata.ui.entity.layout.item.SearchFormViewControl;
import org.iplass.adminconsole.shared.metadata.dto.AdminDefinitionModifyResult;
import org.iplass.adminconsole.shared.metadata.rpc.MetaDataServiceAsync;
import org.iplass.adminconsole.shared.metadata.rpc.MetaDataServiceFactory;
import org.iplass.mtp.definition.DefinitionEntry;
import org.iplass.mtp.entity.definition.EntityDefinition;
import org.iplass.mtp.view.generic.EntityView;
import org.iplass.mtp.view.generic.SearchFormView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 検索画面レイアウト設定用パネル
 * @author lis3wg
 *
 */
public class SearchLayoutPanelImpl extends MetaDataMainEditPane implements SearchLayoutPanel {
	/** メタデータサービス */
	private final MetaDataServiceAsync service = MetaDataServiceFactory.get();

	private EntityDefinition ed;

	/** ヘッダ部分 */
	private MetaCommonHeaderPane headerPane;
	/** 共通属性部分 */
	private MetaCommonAttributeSection<EntityView> commonSection;

	/** メニュー部分のレイアウト */
	private EntityViewMenuPane viewMenuPane;

	private SearchFormViewControl form;

	private EntityView curDefinition;

	/** 編集対象バージョン */
	private int curVersion;

	/** 編集対象ID */
	private String curDefinitionId;

	public SearchLayoutPanelImpl() {
	}

	public void setTarget(final MetaDataItemMenuTreeNode targetNode, EntityPlugin manager) {
		super.setTarget(targetNode, manager);

		headerPane = new MetaCommonHeaderPane(targetNode);
//		headerPane.setSaveClickHandler(new SaveClickHandler());
//		headerPane.setSaveVisible(false);
		headerPane.setSaveDisabled(true);
		headerPane.setSaveHover(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewSaveCautionComment"));
		headerPane.setCancelClickHandler(new CancelClickHandler());
		LayoutSpacer space = new LayoutSpacer();
		space.setWidth(95);
		headerPane.addMember(space);
		// 全削除ボタン
		IButton allDelete = new IButton("All Remove");
		allDelete.addClickHandler(new InitClickHandler());
		SmartGWTUtil.addHoverToCanvas(allDelete, AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_deleteLayoutCautionComment"));
		headerPane.addMember(allDelete);

		headerPane.setHistoryClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MetaDataHistoryDialog metaDataHistoryDialog = new MetaDataHistoryDialog(curDefinition.getClass().getName(), curDefinitionId, curVersion);
				metaDataHistoryDialog.show();
			}
		});

		//共通属性
		commonSection = new MetaCommonAttributeSection<>(targetNode, EntityView.class);

		//View編集画面
		VLayout viewEditPane = new VLayout();

		//メニュー部分
		viewMenuPane = new EntityViewMenuPane();
		viewMenuPane.setSaveClickHandler(new SaveClickHandler());
		viewMenuPane.setDisplayClickHandler(new DisplayClickHandler());
		viewMenuPane.setAddClickHandler(new AddClickHandler());
		viewMenuPane.setDeleteClickHandler(new DeleteClickHandler());
		viewMenuPane.setStandardClickHandler(new StandardClickHandler());
		viewMenuPane.setCopyClickHandler(new CopyClickHandler());

		//データ部分
		HLayout layout = new HLayout();
		layout.setWidth100();

		// 編集用のエリア
		form = new SearchFormViewControl(this.defName);
		form.setShowResizeBar(true);
		form.setResizeBarTarget("next");	//リサイズバーをダブルクリックした際、次を収縮
		layout.addMember(form);

		// ドラッグエリア
		EntityViewDragPane dragArea = new EntityViewDragPane(defName, true, ViewType.SEARCH);
		layout.addMember(dragArea);

		viewEditPane.addMember(viewMenuPane);
		viewEditPane.addMember(layout);

		// Section設定
		SectionStackSection searchViewSection = createSection("Search Views", viewEditPane);

		setMainSections(commonSection, searchViewSection);

		addMember(headerPane);
		addMember(mainStack);

		initializeData();
	}

	private void initializeData() {
		loadEntityDefinition();
	}

	private void loadEntityDefinition() {
		service.getEntityDefinition(TenantInfoHolder.getId(), defName, new AsyncCallback<EntityDefinition>() {

			@Override
			public void onSuccess(EntityDefinition result) {
				ed = result;

				form.setEntityDefinition(result);
				//保存されたメタデータからレイアウト復元
				service.getDefinitionEntry(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new LoadAsyncCallback());
			}

			@Override
			public void onFailure(Throwable caught) {
				SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failedGetEntityDef") + caught.getMessage());
				GWT.log(caught.toString(), caught);
			}
		});
	}

	/**
	 * Viewをリセット。
	 */
	private void reset() {
		form.reset();
	}

	/**
	 * デフォルトViewを再読込
	 */
	private void reloadDefaultView() {
		//エラーのクリア
		commonSection.clearErrors();

		//画面をリセット
		reset();

		//保存されたメタデータからレイアウト復元
		service.getDefinitionEntry(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new LoadAsyncCallback());

		//SharedConfigの再表示
		commonSection.refreshSharedConfig();
	}

	/**
	 * Viewの情報を展開。
	 * @param fv
	 */
	private void apply(SearchFormView fv) {
		form.apply(fv);
	}

	/**
	 * 初期化読込処理
	 */
	private final class LoadAsyncCallback extends AdminAsyncCallback<DefinitionEntry> {

		@Override
		public void onSuccess(DefinitionEntry entry) {
			if (entry == null || entry.getDefinition() == null) {
				//共通属性（Entityからコピー）
				EntityView copy = new EntityView();
				copy.setName(ed.getName());
				copy.setDisplayName(ed.getDisplayName());
				commonSection.setDefinition(copy);

				viewMenuPane.setValueMap(new String[0]);
				viewMenuPane.getViewSelectItem().setValue("");

				//まだ未保存なのでShared設定利用不可
				commonSection.setSharedEditDisabled(true);

				return;
			}

			curDefinition = (EntityView) entry.getDefinition();
			curVersion = entry.getDefinitionInfo().getVersion();
			curDefinitionId = entry.getDefinitionInfo().getObjDefId();

			//共通属性
			commonSection.setDefinition(curDefinition);

			//保存されているのでShared設定利用可能
			commonSection.setSharedEditDisabled(false);

			//作成済みのView名を取得
			viewMenuPane.setValueMap(curDefinition.getSearchFormViewNames());
			viewMenuPane.getViewSelectItem().setValue("");

			//デフォルトのViewを展開
			SearchFormView fv = curDefinition.getDefaultSearchFormView();
			if (fv == null) fv = new SearchFormView();

			apply(fv);

			//ステータスチェック
			StatusCheckUtil.statuCheck(EntityView.class.getName(), defName.replace(".", "/"), SearchLayoutPanelImpl.this);
		}
	}

	/**
	 * 保存ボタンイベント
	 */
	private final class SaveClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			boolean commonValidate = commonSection.validate();

			if (!commonValidate) {
				return;
			}

			SearchFormView fv = form.getForm();
			String name = fv.getName();
			if (name == null || name.isEmpty()) {
				name = EntityViewMenuPane.DEFAULT_VIEW_NAME;
			}
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewName") +name +
					AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_saveView"), new BooleanCallback() {

				@Override
				public void execute(Boolean value) {
					if (value) {
						//最新のView定義を取得
						service.getDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new SaveStartAsyncCallback());
					}
				}
			});
		}

		/**
		 * 編集開始
		 */
		private final class SaveStartAsyncCallback implements AsyncCallback<EntityView> {
			@Override
			public void onSuccess(EntityView ev) {
				//検索画面のデータ作成
				SearchFormView fv = form.getForm();

				//各セクションの情報収集
				if (ev == null) {
					//View定義を新規作成
					EntityView tmp = new EntityView();

					commonSection.getEditDefinition(tmp);
					tmp.setDefinitionName(defName);

					if (fv.getName() == null) {
						tmp.setDefaultSearchFormView(fv);
					} else {
						tmp.setSearchFormView(fv);
					}

					createEntityView(tmp);
				} else {
					//View定義を更新

					commonSection.getEditDefinition(ev);
					ev.setDefinitionName(defName);

					if (fv.getName() == null) {
						ev.setDefaultSearchFormView(fv);
					} else {
						ev.setSearchFormView(fv);
					}

					updateEntityView(ev, true);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				//定義の取得失敗
				SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failedSearchLayout") + caught.getMessage());
				GWT.log(caught.toString(), caught);
			}
		}

		private void createEntityView(final EntityView  definition) {
			SmartGWTUtil.showSaveProgress();
			service.createDefinition(TenantInfoHolder.getId(), definition, new MetaDataUpdateCallback() {
				@Override
				protected void overwriteUpdate() {
				}

				@Override
				protected void afterUpdate(AdminDefinitionModifyResult result) {
					//追加成功
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_completion"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_saveSearchLayoutComp"));

					//ステータスチェック
					StatusCheckUtil.statuCheck(EntityView.class.getName(), defName.replace(".", "/"), SearchLayoutPanelImpl.this);

					//保存されたのでShared設定利用可能
					commonSection.setSharedEditDisabled(false);
				}
			});
		}

		private void updateEntityView(final EntityView  definition, boolean checkVersion) {
			SmartGWTUtil.showSaveProgress();
			service.updateDefinition(TenantInfoHolder.getId(), definition, curVersion, checkVersion, new MetaDataUpdateCallback() {

				@Override
				protected void overwriteUpdate() {
					updateEntityView(definition, false);
				}

				@Override
				protected void afterUpdate(AdminDefinitionModifyResult result) {
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_completion"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_saveSearchLayoutComp"));

					//ステータスチェック
					StatusCheckUtil.statuCheck(EntityView.class.getName(), defName.replace(".", "/"), SearchLayoutPanelImpl.this);

					//保存されたのでShared設定利用可能
					commonSection.setSharedEditDisabled(false);

					// 画面はリロードされないので、最新バージョンを取得
					service.getDefinitionEntry(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new AsyncCallback<DefinitionEntry>() {

						@Override
						public void onFailure(Throwable caught) {
							SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_DetailLayoutPane_failedDetailLayout") + caught.getMessage());
						}

						@Override
						public void onSuccess(DefinitionEntry result) {
							curVersion = result.getDefinitionInfo().getVersion();
						}

					});
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
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_cancelConfirm"),
					AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_cancelCautionComment")
					, new BooleanCallback() {
				@Override
				public void execute(Boolean value) {
					if (value) {
						reloadDefaultView();
					}
				}
			});
		}
	}

	/**
	 * 表示ボタンイベント
	 */
	private final class DisplayClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			//破棄確認
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_cancelCautionComment"), new OkClickHandler());
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {
			@Override
			public void execute(Boolean value) {
				if (value) {
					//OKが押されたら最新の定義を読み込み
					service.getDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new DisplayAsyncCallback());
				}
			}
		}

		/**
		 * 指定Viewの表示処理
		 */
		private final class DisplayAsyncCallback extends AdminAsyncCallback<EntityView> {

			@Override
			public void onSuccess(EntityView ev) {
				//表示対象を取得
				String name = viewMenuPane.getViewName();
				SearchFormView fv = null;
				if (ev != null) {
					if (name == null || name.equals("")) {
						fv = ev.getDefaultSearchFormView();
					} else {
						fv = ev.getSearchFormView(name);
					}
				}

				//画面をリセット
				reset();

				//Viewを展開
				if (fv == null) {
					fv = new SearchFormView();
					fv.setName(name);
				}
				apply(fv);
			}

		}
	}

	/**
	 * 追加ボタンイベント
	 */
	private final class AddClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			service.getDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new AdminAsyncCallback<EntityView>() {

				@Override
				public void onSuccess(EntityView result) {
					if (result != null && result.getDefaultSearchFormView() != null) {
						SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_cancelCautionComment"), new OkClickHandler());
					} else {
						SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_defaultViewCreateCaution"));
					}
				}

			});
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {
			@Override
			public void execute(Boolean value) {
				if (value) {
					//新規Viewの名前を取得
					CreateViewDialog<EntityView> dialog = new CreateViewDialog<>(service, defName, EntityView.class);
					dialog.setOkClickHandler(new AddEventHandler());
					dialog.show();
				}
			}
		}

		/**
		 * View追加処理
		 */
		private final class AddEventHandler implements MTPEventHandler {
			@Override
			public void execute(MTPEvent event) {
				String name = (String) event.getValue("name");
				EntityView ev = (EntityView)  event.getValue("entityView");
				List<String> names = new ArrayList<String>();
				if (ev != null) {
					String[] valueMap = ev.getSearchFormViewNames();
					names.addAll(Arrays.asList(valueMap));
				}

				//存在チェック
				if (names.contains(name)) {
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failed"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewNameAlreadyExis"));
					return;
				}

				//プルダウンに追加
				names.add(name);
				viewMenuPane.setValueMap(names.toArray(new String[names.size()]));
				viewMenuPane.getViewSelectItem().setValue(name);

				//画面をリセット
				reset();

				SearchFormView fv = new SearchFormView();
				fv.setName(name);
				apply(fv);
			}
		}
	}

	/**
	 * 削除ボタンイベント
	 */
	private final class DeleteClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String name = viewMenuPane.getViewName();
			if (name.isEmpty()) {
				name = EntityViewMenuPane.DEFAULT_VIEW_NAME;
			}
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewName") +name +
					AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_deleteViewCautionComment"),
					new OkClickHandler());
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {

			@Override
			public void execute(Boolean value) {
				if (value) {
					//OKが押されたら最新の定義を読み込み
					service.getDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new DeleteStartAsyncCallback());
				}
			}
		}

		/**
		 * 削除開始
		 */
		private final class DeleteStartAsyncCallback extends AdminAsyncCallback<EntityView> {

			@Override
			public void onSuccess(EntityView ev) {
				//プルダウンのView名を元に削除
				String name = viewMenuPane.getViewName();

				if (ev == null) {
					if (name.isEmpty()) {
						name = EntityViewMenuPane.DEFAULT_VIEW_NAME;
					}
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failed"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_selectView") + name +
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_notRegist"));
					return;
				}

				ev.removeSearchForView(name);

				if (name.isEmpty() && ev.getSearchFormViewNames().length > 0) {
					//defaultを削除しても他のViewがある場合は不正なのでNG
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failed"), "[" + EntityViewMenuPane.DEFAULT_VIEW_NAME + "]" +
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewDeleteRemainsComment"));
					return;
				}

				if (ev.getViews().size() > 0) {
					commonSection.getEditDefinition(ev);
					ev.setDefinitionName(defName);
					updateEntityView(ev, true);
				} else {
					service.deleteDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new DeleteEndAsyncCallback());
				}
			}

		}

		private void updateEntityView(final EntityView  definition, boolean checkVersion) {
			SmartGWTUtil.showSaveProgress();
			service.updateDefinition(TenantInfoHolder.getId(), definition, curVersion, checkVersion, new MetaDataUpdateCallback() {

				@Override
				protected void overwriteUpdate() {
					updateEntityView(definition, false);
				}

				@Override
				protected void afterUpdate(AdminDefinitionModifyResult result) {
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_completion"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_deleteComp"));

					//画面を再読込
					reloadDefaultView();
				}
			});
		}
	}

	/**
	 * 全削除ボタンイベント
	 */
	private final class InitClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_allDeleteCautionComment"),
					new OkClickHandler());
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {

			@Override
			public void execute(Boolean value) {
				if (value) {
					//OKが押されたら定義を削除
					service.deleteDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new DeleteEndAsyncCallback());
				}
			}
		}
	}

	/**
	 * 削除終了
	 */
	private final class DeleteEndAsyncCallback implements AsyncCallback<AdminDefinitionModifyResult> {
		@Override
		public void onSuccess(AdminDefinitionModifyResult result) {
			if (result.isSuccess()) {
				SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_completion"),
						AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_deleteComp"));

				//画面を再読込
				reloadDefaultView();
			} else {
				SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failedDeleteEntity") + result.getMessage());
			}
		}

		@Override
		public void onFailure(Throwable caught) {
			SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failedDeleteEntity") + caught.getMessage());
			GWT.log(caught.toString(), caught);
		}
	}

	/**
	 * 標準ロードボタンイベント
	 */
	private final class StandardClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String name = viewMenuPane.getViewName();
			if (name.isEmpty()) {
				name = EntityViewMenuPane.DEFAULT_VIEW_NAME;
			}
			SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewName") +name +
					AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_reflectLayoutStandardDef"), new OkClickHandler());
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {
			@Override
			public void execute(Boolean value) {
				if (value) {
					service.createDefaultSearchFormView(TenantInfoHolder.getId(), defName, new AsyncCallback<SearchFormView>() {

						@Override
						public void onSuccess(SearchFormView result) {
							String name = viewMenuPane.getViewName();
							result.setName(name);

							// 画面をリセット
							reset();

							// Viewを展開
							apply(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							SC.warn(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failedGetDefaultView") + caught.getMessage());
							GWT.log(caught.toString(), caught);
						}
					});
				}
			}
		}
	}

	/**
	 * コピーボタンイベント
	 */
	private final class CopyClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			service.getDefinition(TenantInfoHolder.getId(), EntityView.class.getName(), defName, new AdminAsyncCallback<EntityView>() {

				@Override
				public void onSuccess(EntityView result) {
					if (result != null && result.getDefaultSearchFormView() != null) {
						SC.ask(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_cancelCautionComment"), new OkClickHandler());
					} else {
						SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_defaultViewCreateCaution"));
					}
				}

			});
		}

		/**
		 * 確認ダイアログのイベント
		 */
		private final class OkClickHandler implements BooleanCallback {
			@Override
			public void execute(Boolean value) {
				if (value) {
					//新規Viewの名前を取得
					CreateViewDialog<EntityView> dialog = new CreateViewDialog<>(service, defName, EntityView.class);
					dialog.setOkClickHandler(new AddEventHandler());
					dialog.show();
				}
			}
		}

		/**
		 * View追加処理
		 */
		private final class AddEventHandler implements MTPEventHandler {
			@Override
			public void execute(MTPEvent event) {
				String name = (String) event.getValue("name");
				EntityView ev = (EntityView)  event.getValue("entityView");
				List<String> names = new ArrayList<String>();
				if (ev != null) {
					String[] valueMap = ev.getSearchFormViewNames();
					names.addAll(Arrays.asList(valueMap));
				}

				// 存在チェック
				if (names.contains(name)) {
					SC.say(AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_failed"),
							AdminClientMessageUtil.getString("ui_metadata_entity_layout_SearchLayoutPane_viewNameAlreadyExis"));
					return;
				}

				// プルダウンに追加
				names.add(name);
				viewMenuPane.setValueMap(names.toArray(new String[names.size()]));
				viewMenuPane.getViewSelectItem().setValue(name);

				SearchFormView fv = form.getForm();
				fv.setName(name);

				// 画面をリセット
				reset();

				// viewを展開
				apply(fv);
			}
		}
	}

}
