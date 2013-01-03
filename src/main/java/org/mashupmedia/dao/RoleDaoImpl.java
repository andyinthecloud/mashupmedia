package org.mashupmedia.dao;

import java.util.List;

import org.hibernate.Query;
import org.mashupmedia.model.Role;
import org.springframework.stereotype.Repository;

@Repository
public class RoleDaoImpl extends BaseDaoImpl implements RoleDao {

	@Override
	public void saveRole(Role role) {
		sessionFactory.getCurrentSession().saveOrUpdate(role);

	}

	@Override
	public Role getRole(String idName) {
		Query query = sessionFactory.getCurrentSession().createQuery("from Role where idName = :idName");
		query.setString("idName", idName);
		query.setCacheable(true);
		Role role = (Role) query.uniqueResult();
		return role;
	}

	@Override
	public List<Role> getRoles() {
		Query query = sessionFactory.getCurrentSession().createQuery("from Role order by name");
		query.setCacheable(true);
		@SuppressWarnings("unchecked")
		List<Role> roles = query.list();
		return roles;
	}

}
