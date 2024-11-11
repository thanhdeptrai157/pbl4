package org.Server.Model.DAL.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Server.Model.DTO.ClassPeriod;

public class ClassesPeriodMapper implements RowMapper<ClassPeriod>{

	@Override
	public ClassPeriod mapRow(ResultSet rs) {
		try {
			ClassPeriod classes = new ClassPeriod();
			
			classes.setId_class_period(rs.getInt("id_class_period"));
			classes.setId_class(rs.getInt("id_class"));
			classes.setTime_start(rs.getDate("time_start"));
			classes.setTime_end(rs.getDate("time_end"));
			
			return classes;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
