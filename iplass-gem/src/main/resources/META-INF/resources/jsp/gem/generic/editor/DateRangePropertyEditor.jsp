<%--
 Copyright (C) 2017 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.

 Unless you have purchased a commercial license,
 the following license terms apply:

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program. If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true"%>
<%@ page import="org.iplass.mtp.ManagerLocator"%>
<%@ page import="org.iplass.mtp.entity.Entity"%>
<%@ page import="org.iplass.mtp.entity.definition.*" %>
<%@ page import="org.iplass.mtp.entity.definition.properties.ReferenceProperty"%>
<%@ page import="org.iplass.mtp.util.StringUtil"%>
<%@ page import="org.iplass.mtp.view.generic.EntityViewUtil" %>
<%@ page import="org.iplass.mtp.view.generic.ViewConst"%>
<%@ page import="org.iplass.mtp.view.generic.editor.DatePropertyEditor"%>
<%@ page import="org.iplass.mtp.view.generic.editor.DateRangePropertyEditor" %>
<%@ page import="org.iplass.mtp.view.generic.editor.PropertyEditor" %>
<%@ page import="org.iplass.mtp.view.generic.editor.DateTimePropertyEditor"%>
<%@ page import="org.iplass.mtp.view.generic.editor.TimePropertyEditor"%>
<%@ page import="org.iplass.mtp.view.generic.editor.TimestampPropertyEditor"%>
<%@ page import="org.iplass.mtp.view.generic.OutputType"%>
<%@ page import="org.iplass.mtp.web.template.TemplateUtil"%>
<%@ page import="org.iplass.gem.command.Constants"%>
<%@ page import="org.iplass.gem.command.GemResourceBundleUtil"%>
<%!
String getObjectName(String prefix, EntityDefinition ed){
	String objName = "";
	if (prefix.contains("[")) {
		prefix = prefix.substring(0, prefix.lastIndexOf("["));
	}

	int lastIndex = prefix.lastIndexOf(".");
	String propName = (lastIndex == -1) ? prefix  : prefix.substring(0, lastIndex);
	String _propName = (lastIndex == -1) ? "" : prefix.substring(lastIndex + 1);

	PropertyDefinition pd = ed.getProperty(propName);
	ReferenceProperty rp = (ReferenceProperty) pd;
	if (rp != null) {
		objName = rp.getObjectDefinitionName();
	} else {
		return this.getObjectName(_propName, ed);
	}

	if (_propName.length() > 0) {
		ed = ManagerLocator.getInstance().getManager(EntityDefinitionManager.class).get(objName);
		objName = this.getObjectName(_propName, ed);
	}

	return objName;
}

%>
<%
	DateRangePropertyEditor editor = (DateRangePropertyEditor) request.getAttribute(Constants.EDITOR_EDITOR);
	OutputType type = (OutputType) request.getAttribute(Constants.OUTPUT_TYPE);
	EntityDefinition ed = (EntityDefinition) request.getAttribute(Constants.ENTITY_DEFINITION);
	String rootDefName = (String)request.getAttribute(Constants.ROOT_DEF_NAME);
	Boolean nest = (Boolean) request.getAttribute(Constants.EDITOR_REF_NEST);
	nest = (nest != null) ? nest : false;
	Entity entity = request.getAttribute(Constants.ENTITY_DATA) instanceof Entity ? (Entity) request.getAttribute(Constants.ENTITY_DATA) : null;

	String prefix = "";
	String fromName = editor.getPropertyName();
	EntityDefinition _ed = ed;
	//if (nest != null && nest) {
	if (fromName.indexOf(".") != -1) {
		//ダミーのプロパティ名と本来のプロパティ名を分離して、出力時に結合するプロパティを含め再設定する
		int lastIndex = fromName.lastIndexOf(".");
		String _prefix = fromName.substring(0, lastIndex);
		prefix = _prefix + ".";
		fromName = fromName.substring(lastIndex + 1);
		request.setAttribute(Constants.EDITOR_JOIN_NEST_PREFIX, _prefix);
		String subObjectName = getObjectName(_prefix, ed);

		_ed = ManagerLocator.getInstance().getManager(EntityDefinitionManager.class).get(subObjectName);
		request.setAttribute(Constants.ENTITY_DEFINITION, _ed);
		if (nest) {
			entity = (Entity) request.getAttribute(Constants.EDITOR_REF_NEST_VALUE);
		}
	}

	String key = "daterange_" + fromName;
	PropertyEditor fromEditor = editor.getEditor();
	String path = EntityViewUtil.getJspPath(fromEditor, ViewConst.DESIGN_TYPE_GEM);

	// from
	fromEditor.setPropertyName(prefix + fromName);
	request.setAttribute(Constants.EDITOR_EDITOR, fromEditor);
	String fromKey = fromEditor.getPropertyName().replaceAll("\\.", "_");

	if (OutputType.SEARCHCONDITION == type) {
		((DateTimePropertyEditor) editor.getEditor()).setHideSearchConditionTo(true);
	}
%>
<span class="dateRange">
<span id="daterange_<c:out value="<%=fromKey%>" />">
<jsp:include page="<%=path%>" />
</span>
<%
	// to
	if (StringUtil.isNotBlank(editor.getToPropertyName()) && OutputType.SEARCHCONDITION != type) {
		Object toPropValue = ( entity != null ) ? entity.getValue(editor.getToPropertyName()) : null;
		// toのEditorが未指定の場合はfromと同じ設定に
		PropertyEditor toEditor = editor.getToEditor() != null ? editor.getToEditor() : editor.getEditor();
		toEditor.setPropertyName(prefix + editor.getToPropertyName());
		String toName = "";
		PropertyDefinition toPd = null;
		if (prefix.length() != 0) {
			toName = editor.getToPropertyName().substring(editor.getToPropertyName().lastIndexOf(".") + 1);
			toPd = _ed.getProperty(toName);
		} else {
			toPd = _ed.getProperty(editor.getToPropertyName());
		}

		request.setAttribute(Constants.EDITOR_EDITOR, toEditor);
		request.setAttribute(Constants.EDITOR_PROP_VALUE, toPropValue);
		request.setAttribute(Constants.EDITOR_PROPERTY_DEFINITION, toPd);
		String toKey = toEditor.getPropertyName().replaceAll("\\.", "_");

		String dash = GemResourceBundleUtil.resourceString("generic.editor.DateRangePropertyEditor.range");

%>
<span id="daterange_<c:out value="<%=toKey%>" />">
<c:out value="<%=dash %>"></c:out>
<jsp:include page="<%=path%>" />
</span>
<%
	}
	String nameKey = prefix + fromName + "_" + editor.getToPropertyName();
%>
</span>
<jsp:include page="ErrorMessage.jsp">
	<jsp:param value="<%=nameKey%>" name="propName" />
</jsp:include>
<%
	if (OutputType.SEARCHCONDITION == type) {
		String errorMessage = TemplateUtil.getMultilingualString(editor.getErrorMessage(), editor.getLocalizedErrorMessageList());
		if (errorMessage == null) errorMessage = GemResourceBundleUtil.resourceString("generic.editor.DateRangePropertyEditor.invalitDateRange");
%>
<%
	}
	request.setAttribute(Constants.ENTITY_DEFINITION, ed);

	request.removeAttribute(Constants.EDITOR_EDITOR);
	request.removeAttribute(Constants.EDITOR_PROP_VALUE);
	request.removeAttribute(Constants.EDITOR_PROPERTY_DEFINITION);
%>
