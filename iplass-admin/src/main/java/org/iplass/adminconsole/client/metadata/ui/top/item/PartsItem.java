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

package org.iplass.adminconsole.client.metadata.ui.top.item;

import org.iplass.adminconsole.client.base.ui.widget.AbstractWindow;

/**
 *
 * @author lis3wg
 */
public abstract class PartsItem extends AbstractWindow {

	protected String dropAreaType;

	/**
	 * コンストラクタ
	 */
	public PartsItem() {
		setWidth100();
		setHeight(22);
		setCanDragReposition(true);
		setCanDrop(true);
		setShowEdges(false);
		setShowMinimizeButton(false);
		setBorder("1px solid navy");

		// ダミーで空のスタイルを指定しないとsmartget3.1からはsetBackgroundColorが有効にならない
		setStyleName("");
	}

	public abstract <T> T getParts();

	public void setDropAreaType(String dropAreaType) {
		this.dropAreaType = dropAreaType;
	}

	public void doDropAction(DropActionCallback callback) {
		if (callback != null) callback.handle();
	}

	public interface DropActionCallback {
		void handle();
	}
}
