package org.Server.Model.DAL.Specification;

public interface Specification <T>{
	String getQuery();
	void setParameters(Object...objects);
	Object[] getParameters();
}
