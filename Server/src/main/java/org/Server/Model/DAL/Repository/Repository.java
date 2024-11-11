package org.Server.Model.DAL.Repository;

import java.util.List;

import org.Server.Model.DAL.Specification.Specification;


public interface Repository<T> {
	void add(T t);	
	List<T> getALL();
	List<T> findBySpacification(Specification<T> specification);
	void update(T t);
	void remove(T t);
}
