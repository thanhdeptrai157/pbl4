package org.Server.Model.DAL.Specification.Implements;

import org.Server.Model.DAL.Specification.Specification;
import org.Server.Model.DTO.Classes;

public class FindClassByID implements Specification<Classes>{

	private int id;
	private Object[] objects;
	
	public FindClassByID(int id) {
		this.id = id;
	}
	
	@Override
	public String getQuery() {
		String query = "SELECT * FROM class WHERE id_class = ?";
		
		setParameters(id);
		
		return query;
	}

	@Override
	public void setParameters(Object... objects) {
		this.objects = objects;
	}

	@Override
	public Object[] getParameters() {
		return objects;
	}

}
