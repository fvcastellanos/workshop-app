package net.cavitos.workshop.service;

import net.cavitos.workshop.domain.exception.BusinessException;
import net.cavitos.workshop.domain.model.web.CarBrand;
import net.cavitos.workshop.model.entity.CarBrandEntity;
import net.cavitos.workshop.model.repository.CarBrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static net.cavitos.workshop.domain.model.status.ActiveStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarBrandServiceTest {

    @Mock
    private CarBrandRepository carBrandRepository;

    @InjectMocks
    private CarBrandService carBrandService;

    @Test
    void getAllByTenant_shouldReturnRepositoryPage() {
        String tenant = "tenant-1";
        int active = ACTIVE.value();
        String name = "to";
        int page = 0;
        int size = 10;

        Page<CarBrandEntity> expected = new PageImpl<>(List.of(entity("id-1", "TOYOTA", tenant, active)));

        when(carBrandRepository.findByTenantAndActiveAndNameContainsIgnoreCase(
                eq(tenant), eq(active), eq(name), eq(PageRequest.of(page, size)))).thenReturn(expected);

        Page<CarBrandEntity> result = carBrandService.getAllByTenant(tenant, active, name, page, size);

        assertThat(result).isSameAs(expected);
        verify(carBrandRepository).findByTenantAndActiveAndNameContainsIgnoreCase(
                eq(tenant), eq(active), eq(name), eq(PageRequest.of(page, size)));

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void getById_whenEntityExistsAndTenantMatches_shouldReturnEntity() {
        String tenant = "tenant-1";
        String id = "id-1";
        CarBrandEntity existing = entity(id, "TOYOTA", tenant, ACTIVE.value());

        when(carBrandRepository.findById(id)).thenReturn(Optional.of(existing));

        CarBrandEntity result = carBrandService.getById(tenant, id);

        assertThat(result).isSameAs(existing);
        verify(carBrandRepository).findById(id);

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void getById_whenEntityMissing_shouldThrowNotFoundBusinessException() {
        String tenant = "tenant-1";
        String id = "missing-id";

        when(carBrandRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carBrandService.getById(tenant, id))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("car_brand_id=missing-id not found for tenant=tenant-1");
                });

        verify(carBrandRepository).findById(id);

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void getById_whenTenantMismatch_shouldThrowUnprocessableEntityBusinessException() {
        String id = "id-1";
        CarBrandEntity existing = entity(id, "TOYOTA", "another-tenant", ACTIVE.value());

        when(carBrandRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> carBrandService.getById("tenant-1", id))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Invalid Car Brand");
                });

        verify(carBrandRepository).findById(id);

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void add_whenBrandAlreadyExists_shouldReturnExistingWithoutSaving() {
        String tenant = "tenant-1";
        CarBrand request = request("toyota", "desc", 0);
        CarBrandEntity existing = entity("id-1", "TOYOTA", tenant, ACTIVE.value());

        when(carBrandRepository.findByNameAndTenant("TOYOTA", tenant)).thenReturn(Optional.of(existing));

        CarBrandEntity result = carBrandService.add(tenant, request);

        assertThat(result).isSameAs(existing);
        verify(carBrandRepository).findByNameAndTenant("TOYOTA", tenant);
        verify(carBrandRepository, never()).save(org.mockito.ArgumentMatchers.any(CarBrandEntity.class));

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void add_whenBrandIsNew_shouldUppercaseAndSaveNewEntity() {
        String tenant = "tenant-1";
        CarBrand request = request("toyota", "desc", 0);

        when(carBrandRepository.findByNameAndTenant("TOYOTA", tenant)).thenReturn(Optional.empty());
        when(carBrandRepository.save(org.mockito.ArgumentMatchers.any(CarBrandEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarBrandEntity result = carBrandService.add(tenant, request);

        ArgumentCaptor<CarBrandEntity> entityCaptor = ArgumentCaptor.forClass(CarBrandEntity.class);
        verify(carBrandRepository).findByNameAndTenant("TOYOTA", tenant);
        verify(carBrandRepository).save(entityCaptor.capture());

        CarBrandEntity savedEntity = entityCaptor.getValue();
        assertThat(savedEntity.getId()).isNotBlank();
        assertThat(savedEntity.getName()).isEqualTo("TOYOTA");
        assertThat(savedEntity.getDescription()).isEqualTo("desc");
        assertThat(savedEntity.getTenant()).isEqualTo(tenant);
        assertThat(savedEntity.getCreated()).isNotNull();
        assertThat(savedEntity.getActive()).isEqualTo(ACTIVE.value());

        assertThat(result).isSameAs(savedEntity);

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void update_whenBrandNotFound_shouldThrowNotFoundBusinessException() {
        String tenant = "tenant-1";
        String id = "missing-id";

        when(carBrandRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carBrandService.update(tenant, id, request("honda", "desc", 0)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(businessException.getMessage()).isEqualTo("Car Brand Not Found");
                });

        verify(carBrandRepository).findById(id);
        verify(carBrandRepository, never()).save(org.mockito.ArgumentMatchers.any(CarBrandEntity.class));

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void update_whenTenantMismatch_shouldThrowUnprocessableEntityBusinessException() {
        String id = "id-1";
        CarBrandEntity existing = entity(id, "TOYOTA", "other-tenant", ACTIVE.value());

        when(carBrandRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> carBrandService.update("tenant-1", id, request("honda", "desc", 0)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> {
                    BusinessException businessException = (BusinessException) ex;
                    assertThat(businessException.getHttpStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(businessException.getMessage()).isEqualTo("Car Brand not found");
                });

        verify(carBrandRepository).findById(id);
        verify(carBrandRepository, never()).save(org.mockito.ArgumentMatchers.any(CarBrandEntity.class));

        verifyNoMoreInteractions(carBrandRepository);
    }

    @Test
    void update_whenValidRequest_shouldMutateAndSaveEntity() {
        String tenant = "tenant-1";
        String id = "id-1";
        CarBrandEntity existing = entity(id, "TOYOTA", tenant, ACTIVE.value());

        when(carBrandRepository.findById(id)).thenReturn(Optional.of(existing));
        when(carBrandRepository.save(existing)).thenReturn(existing);

        CarBrandEntity result = carBrandService.update(tenant, id, request("honda", "updated", 0));

        assertThat(result).isSameAs(existing);
        assertThat(existing.getName()).isEqualTo("HONDA");
        assertThat(existing.getDescription()).isEqualTo("updated");
        assertThat(existing.getActive()).isZero();

        verify(carBrandRepository).findById(id);
        verify(carBrandRepository).save(existing);

        verifyNoMoreInteractions(carBrandRepository);
    }

    private static CarBrandEntity entity(String id, String name, String tenant, int active) {
        return CarBrandEntity.builder()
                .id(id)
                .name(name)
                .description("desc")
                .active(active)
                .tenant(tenant)
                .build();
    }

    private static CarBrand request(String name, String description, int active) {
        CarBrand carBrand = new CarBrand();
        carBrand.setName(name);
        carBrand.setDescription(description);
        carBrand.setActive(active);
        return carBrand;
    }
}

