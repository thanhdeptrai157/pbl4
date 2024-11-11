package org.Server.Model.DAL.Mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.Server.Model.DTO.HistoryWeb;


public class HistoryWebMapper implements RowMapper<HistoryWeb>{

	@Override
	public HistoryWeb mapRow(ResultSet rs) {
		try {
            HistoryWeb history = new HistoryWeb();

            history.setId_class_period(rs.getInt("id_class_period"));
            history.setId_computer(rs.getInt("id_computer"));
            history.setUrl(rs.getString("url"));
            history.setTime_search(rs.getDate("time_search"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
