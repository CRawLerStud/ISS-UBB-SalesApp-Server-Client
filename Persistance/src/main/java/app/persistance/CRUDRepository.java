package app.persistance;

import app.persistance.utils.RepositoryException;

import java.util.List;

public interface CRUDRepository<ID, T> {

    public ID save(T entity) throws RepositoryException;
    public T delete(ID id) throws RepositoryException;
    public T findOne(ID id) throws RepositoryException;
    public List<T> findAll();

}
