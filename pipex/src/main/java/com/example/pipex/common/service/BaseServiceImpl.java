package com.example.pipex.common.service;

import java.lang.reflect.Field;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.pipex.common.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BaseServiceImpl<T, ID> implements BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found id=" + id));
    }

    @Override
    public T create(T dto) {
        return repository.save(dto);
    }

    @Override
    public T update(ID id, T dto) {
        T existingEntity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found id=" + id));

        // Merge non-null fields from dto into existing entity
        ObjectUtils.merge(dto, existingEntity);

        // Ensure ID is set to prevent creating new record
        setIdField(existingEntity, id);

        return repository.save(existingEntity);
    }

    @Override
    public void delete(ID id) {
        repository.deleteById(id);
    }

    /**
     * Set ID field on entity using reflection
     */
    @SuppressWarnings("unchecked")
    private void setIdField(T entity, ID id) {
        try {
            Class<?> clazz = entity.getClass();
            while (clazz != null && clazz != Object.class) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals("id") && field.getType().isAssignableFrom(id.getClass())) {
                        field.setAccessible(true);
                        field.set(entity, id);
                        return;
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (Exception e) {
            log.warn("Failed to set ID field on entity: {}", e.getMessage());
        }
    }

}
