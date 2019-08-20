package org.iplass.mtp.view.generic;

import java.util.ArrayList;
import java.util.List;

import org.iplass.mtp.entity.Entity;
import org.iplass.mtp.entity.ValidateError;

/**
 *  BulkOperationInterrupterの実行結果を保持するContextです。
 *
 */
public class BulkOperationContext {

	/**
	 * バリデーションエラー
	 */
	private List<ValidateError> errors;

	/**
	 * 一括操作対象エンティティリスト
	 */
	private List<Entity> entities;


	public BulkOperationContext(List<Entity> entities) {
		this.entities = entities;
	}

	public BulkOperationContext(List<Entity> entities, List<ValidateError> errors) {
		this.errors = errors;
		this.entities = entities;
	}

	/**
	 * バリデーションエラーリストを取得します。
	 * @return バリデーションエラー
	 */
	public List<ValidateError> getErrors() {
		if (errors == null) {
			errors = new ArrayList<ValidateError>();
		}
		return errors;
	}

	/**
	 * バリデーションエラーを設定します。
	 * @param errors バリデーションエラー
	 */
	public void setErrors(List<ValidateError> errors) {
		this.errors = errors;
	}

	/**
	 * 一括操作対象エンティティリストを取得します。
	 * @return 一括操作対象エンティティリスト
	 */
	public List<Entity> getEntities() {
		if (entities == null) {
			entities = new ArrayList<Entity>();
		}
		return entities;
	}

	/**
	 * 一括操作対象エンティティリストを設定します。
	 * @param entities 一括操作対象エンティティリスト
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
