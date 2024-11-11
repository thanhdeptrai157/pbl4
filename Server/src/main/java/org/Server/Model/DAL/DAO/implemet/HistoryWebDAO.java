package org.Server.Model.DAL.DAO.implemet;

import java.util.List;

import org.Server.Model.DAL.DAO.AbstractDao;
import org.Server.Model.DAL.Mapper.HistoryWebMapper;
import org.Server.Model.DAL.Repository.Repository;
import org.Server.Model.DAL.Specification.Specification;
import org.Server.Model.DTO.HistoryWeb;


public class HistoryWebDAO extends AbstractDao implements Repository<HistoryWeb>{
    @Override
    public void add(HistoryWeb t) {
        String query = "INSERT INTO history_web VALUES (?, ?, ?, ?)";

        insert(query, t.getId_class_period(), t.getId_computer(), t.getUrl(), t.getTime_search());
    }

    @Override
    public List<HistoryWeb> getALL() {
        String query = "SELECT * FROM history_web ";
        List<HistoryWeb> list = query(query, new HistoryWebMapper());
        return list;
    }

    @Override
    public List<HistoryWeb> findBySpacification(Specification<HistoryWeb> specification) {
        List<HistoryWeb> list = query(specification.getQuery(), new HistoryWebMapper(), specification.getParameters());

        return list;
    }

    @Override
    public void update(HistoryWeb t) {
    }

    @Override
    public void remove(HistoryWeb t) {
        String query = "DELETE FROM history_web WHERE id_class_period = ?";
        update(query, t.getId_class_period());
    }


}
