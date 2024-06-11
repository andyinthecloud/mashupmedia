package org.mashupmedia.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.mashupmedia.comparator.MetaEntityComparator;
import org.mashupmedia.model.MetaEntity;

public class MetaEntityHelper<T extends MetaEntity> {

    /**
     * Merge and return removed entities
     * @param entities
     * @param updatedEntities
     * @return entities removed
     */
    public Set<T> mergeSet(Set<T> entities, Set<T> updatedEntities) {

        for (T entity : entities) {
            Optional<? extends MetaEntity> foundEntity = updatedEntities.stream()
                    .filter(e -> e.getId() == entity.getId())
                    .findAny();

            if (foundEntity.isPresent()) {
                entity.updateValues(foundEntity.get());
            }
        }

        Set<T> entitiesToRemove = entities.stream()
                .filter(entity -> !includedIn(entity, updatedEntities))
                .collect(Collectors.toSet());
        entities.removeAll(entitiesToRemove);

        Set<T> entitiesToAdd = updatedEntities.stream()
                .filter(entity -> entity.getId() == 0)
                .collect(Collectors.toSet());

        entities.addAll(entitiesToAdd);

        setEntityRanks(entities);

        return entitiesToRemove;

    }

    private boolean includedIn(T entity, Collection<T> updatedEntities) {
        return updatedEntities.stream()
                .anyMatch(e -> e.getId() == entity.getId());
    }

    private void setEntityRanks(Set<T> entities) {

        List<T> sortedEntities = getSortedEntities(entities);
        for (int i = 0; i < sortedEntities.size(); i++) {
            T sortedEntity = sortedEntities.get(i);
            sortedEntity.setRank(i + 1);
        }

    }

    public List<T> getSortedEntities(Set<T> entities) {
        List<T> sortedEntities = new ArrayList<>(entities);
        Collections.sort(sortedEntities, new MetaEntityComparator());
        return sortedEntities;
    }

    public T getDefaultEntity(Set<T> entities) {
		List<T> metaImages = getSortedEntities(entities);
		return metaImages.isEmpty()
				? null
				: metaImages.get(0);
	}

    public Set<T> addMetaEntity(T entity, Set<T> entities) {
        if (entities == null) {
            entities = new HashSet<>();
        }

        if (entity == null) {
			return entities;
		}

        entity.setRank(entities.size() + 1);

        entities.add(entity);
        return entities;
    }
}
