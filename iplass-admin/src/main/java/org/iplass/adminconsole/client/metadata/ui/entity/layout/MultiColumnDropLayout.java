/*
 * Copyright (C) 2013 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
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
import java.util.List;

import org.iplass.adminconsole.client.metadata.ui.entity.layout.item.ItemControl;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.DropHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VStack;

public abstract class MultiColumnDropLayout extends HLayout {

	//getMemberの代替用
	private List<ColumnLayout> cols = new ArrayList<ColumnLayout>();

	/**
	 * コンストラクタ
	 */
	public MultiColumnDropLayout() {
		this(1);
	}

	/**
	 * コンストラクタ
	 * @param numColumns
	 */
	public MultiColumnDropLayout(int numColumns) {
		setMembersMargin(6);
		setWidth100();
		for (int i = 0; i < numColumns; i++) {
			addColumn(new ColumnLayout());
		}
	}

	/**
	 * 要素を追加
	 * @param component
	 * @param colNum
	 */
	public void addElement(Canvas component, int colNum) {
		cols.get(colNum).addMember(component);
	}

	/**
	 * 列追加
	 */
	private void addColumn(ColumnLayout col) {
		cols.add(col);
		addMember(col);
	}

	@Override
	public Canvas setDropTypes(String types) {
		Canvas canvas = super.setDropTypes(types);
		for (ColumnLayout col : cols) {
			col.setDropTypes(types);
		}
		return canvas;
	}

	@Override
	public Canvas setDropTypes(String... types) {
		Canvas canvas = super.setDropTypes(types);
		for (ColumnLayout col : cols) {
			col.setDropTypes(types);
		}
		return canvas;
	}

	/**
	 * パネル内の行数を取得
	 * @return
	 */
	public int getRowNum() {
		int ret = 0;
		for (ColumnLayout col : cols) {
			//一番行が多い列のメンバ数を取得
			if (ret < col.getMembers().length) {
				ret = col.getMembers().length;
			}
		}
		return ret;
	}

	/**
	 * パネルの列数を取得
	 * @return
	 */
	public int getColNum() {
		return cols.size();
	}

	/**
	 * 指定行列のメンバを返す
	 * @param col
	 * @param row
	 * @return
	 */
	public ItemControl getMember(int col, int row) {
		//getMemberでループしてcol番目のカラムのrow行目のメンバを返す
		int current = 0;
		for (int i = 0; i < getMembers().length; i++) {
			if (getMember(i) instanceof ColumnLayout) {
				if (current == col) {
					ColumnLayout column = (ColumnLayout) getMember(i);
					if (column.getMembers().length > row) {
						return (ItemControl) column.getMember(row);
					}
				}
				current++;
			}
		}
		return null;
	}

	/**
	 * レイアウトの初期化。
	 */
	public void clear() {
		for (ColumnLayout col : cols) {
			for (Canvas canvas : col.getMembers()) {
				if (canvas instanceof ItemControl) {
					((ItemControl) canvas).destroy();
				}
			}
		}
	}

	protected abstract DropHandler getDropHandler();

	/**
	 * レイアウト内の列を表す
	 */
	public class ColumnLayout extends VStack {

		private ColumnLayout() {
			setWidth100();
			setMargin(4);
			setMembersMargin(6);
			setCanAcceptDrop(true);
			setDropLineThickness(4);

			//ドロップ先を表示する設定
			Canvas dropLineProperties = new Canvas();
			dropLineProperties.setBackgroundColor("aqua");
			setDropLineProperties(dropLineProperties);

			//ドラッグ中のコンポーネントを表示する設定
			setShowDragPlaceHolder(true);
			Canvas placeHolderProperties = new Canvas();
			placeHolderProperties.setBorder("2px solid #8289A6");
			setPlaceHolderProperties(placeHolderProperties);

			addDropHandler(getDropHandler());
		}
	}
}
