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

import org.iplass.adminconsole.client.base.event.MTPEvent;
import org.iplass.adminconsole.client.base.event.MTPEventHandler;
import org.iplass.adminconsole.client.base.i18n.AdminClientMessageUtil;
import org.iplass.adminconsole.client.base.rpc.AdminAsyncCallback;
import org.iplass.adminconsole.client.base.tenant.TenantInfoHolder;
import org.iplass.adminconsole.client.base.ui.widget.MtpDialog;
import org.iplass.adminconsole.client.base.ui.widget.form.MtpForm;
import org.iplass.adminconsole.client.base.ui.widget.form.MtpTextItem;
import org.iplass.adminconsole.client.base.util.SmartGWTUtil;
import org.iplass.adminconsole.shared.metadata.rpc.MetaDataServiceAsync;
import org.iplass.mtp.definition.Definition;

import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;

/**
 * View追加用ダイアログ
 * @author lis3wg
 *
 */
public class CreateViewDialog<D extends Definition> extends MtpDialog {

	/** 名前入力用アイテム */
	private TextItem nameItem;
	private DynamicForm form;

	/** OKボタン押下後の処理 */
	private MTPEventHandler okClickHandler;

	private Class<D> defClass;

	/**
	 * コンストラクタ
	 * @param service
	 * @param defName
	 * @param type
	 */
	public CreateViewDialog(final MetaDataServiceAsync service, final String defName, final Class<D> defClass) {
		this.defClass = defClass;

		setTitle(AdminClientMessageUtil.getString("ui_metadata_entity_layout_CreateViewDialog_createNewView"));
		setHeight(140);
		centerInPage();

		nameItem = new MtpTextItem("name", "View Name");
		SmartGWTUtil.setRequired(nameItem);

		form = new MtpForm();
		form.setAutoFocus(true);
		form.setItems(nameItem);

		container.addMember(form);

		IButton ok = new IButton("OK");
		ok.addClickHandler(new OkClickHandler(service, defName));
		IButton cancel = new IButton("Cancel");
		cancel.addClickHandler(new CancelClickHandler());

		footer.setMembers(ok, cancel);
	}

	/**
	 * OKボタンイベント
	 */
	private final class OkClickHandler implements ClickHandler {
		private final MetaDataServiceAsync service;
		private final String defName;

		private OkClickHandler(MetaDataServiceAsync service, String defName) {
			this.service = service;
			this.defName = defName;
		}

		public void onClick(ClickEvent event) {

			if (!form.validate()) {
				return;
			}

			//View定義を取得
			service.getDefinition(TenantInfoHolder.getId(), defClass.getName(), defName, new AdminAsyncCallback<D>() {

				@Override
				public void onSuccess(D ev) {
					//作成済みのView定義名取得
					String name = nameItem.getValue().toString();

					//プルダウンに追加、処理は画面の方に作成
					if (okClickHandler != null) {
						MTPEvent event = new MTPEvent();
						event.setValue("name", name);
						event.setValue("entityView", ev);
						okClickHandler.execute(event);
					}

					destroy();
				}
			});
		}
	}

	/**
	 * Cancelボタンイベント
	 */
	private final class CancelClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			destroy();
		}
	}

	/**
	 * OKボタン押下後の処理を設定
	 * @param handler
	 */
	public void setOkClickHandler(MTPEventHandler handler) {
		this.okClickHandler = handler;
	}
}

