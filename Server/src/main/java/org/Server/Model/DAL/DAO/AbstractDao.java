package org.Server.Model.DAL.DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.Server.Connect.JDBC_Unit;
import org.Server.Model.DAL.Mapper.RowMapper;



public class AbstractDao implements GenericDAO{
	private Connection cnn;

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... parameters) {
		List<T> results = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = JDBC_Unit.getConnection();
			statement = connection.prepareStatement(sql);
			setParameter(statement, parameters);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				results.add(rowMapper.mapRow(resultSet));
			}
			
			return results;
		} catch (SQLException e) {
			System.out.println(e);
			return null;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				return null;
			}
		}
	}
	

	@Override
	public void update(String sql, Object... parameters) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = JDBC_Unit.getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql);
			setParameter(statement, parameters);
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			System.out.println(e);
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		
	}

	@Override
	public int insert(String sql, Object... parameters) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			int id = 0;
			
			connection = JDBC_Unit.getConnection();
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			setParameter(statement, parameters);
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			connection.commit();
			return id;
		} catch (SQLException e) {
			System.out.println(e);
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return 0;
	}

	@Override
	public int count(String sql, Object... parameters) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			int count = 0;
			connection = JDBC_Unit.getConnection();
			statement = connection.prepareStatement(sql);
			setParameter(statement, parameters);
			resultSet = statement.executeQuery();
			while (resultSet.next()) {
				count = resultSet.getInt(1);
			}
			return count;
		} catch (SQLException e) {
			return 0;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
				if (statement != null) {
					statement.close();
				}
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (SQLException e) {
				return 0;
			}
		}
	}
	
	protected void setParameter(PreparedStatement statement, Object... parameters) {
		try {
			if(parameters != null) {
				for (int i = 0; i < parameters.length; i++) {
					Object parameter = parameters[i];
					int index = i + 1;
					if (parameter instanceof Long) {
						statement.setLong(index, (Long) parameter);
					}else if (parameter instanceof String && ((String) parameter).equals("NULL")) {
						statement.setString(index, null);
					} else if (parameter instanceof String) {
						statement.setString(index, (String) parameter);
					} else if (parameter instanceof Integer) {
						statement.setInt(index, (Integer) parameter);
					} else if (parameter instanceof Boolean) {
						statement.setBoolean(index, (Boolean) parameter);
					} else if (parameter instanceof Date) {
						statement.setDate(index, (Date) parameter);
					} else if (parameter instanceof Timestamp) {
						statement.setTimestamp(index, (Timestamp) parameter);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
