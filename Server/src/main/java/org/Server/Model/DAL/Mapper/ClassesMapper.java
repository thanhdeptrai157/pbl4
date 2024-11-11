package org.Server.Model.DAL.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Server.Model.DTO.Classes;

public class ClassesMapper implements RowMapper<Classes>{

	@Override
	public Classes mapRow(ResultSet rs) {
		try {
			Classes classes = new Classes();
			
			classes.setId_class(rs.getInt("id_class"));
			classes.setName_class(rs.getString("name_class"));
			
			return classes;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
