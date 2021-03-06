package stupnytskiy.rostyslav.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import stupnytskiy.rostyslav.demo.dto.request.PaginationRequest;
import stupnytskiy.rostyslav.demo.dto.request.StreetTypeRequest;
import stupnytskiy.rostyslav.demo.dto.response.PageResponse;
import stupnytskiy.rostyslav.demo.dto.response.StreetTypeResponse;
import stupnytskiy.rostyslav.demo.entity.StreetType;
import stupnytskiy.rostyslav.demo.repository.StreetTypeRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StreetTypeService {

    @Autowired
    private StreetTypeRepository streetTypeRepository;

    public void save(StreetTypeRequest request){
        streetTypeRepository.save(streetTypeRequestToStreetType(request,null));
    }

    public void update(StreetTypeRequest request, Long id){
        streetTypeRepository.save(streetTypeRequestToStreetType(request,findById(id)));
    }

    public PageResponse<StreetTypeResponse> findPage(PaginationRequest request){
        final Page<StreetType> page = streetTypeRepository.findAll(request.mapToPageable());
        return new PageResponse<>(page.getContent().stream().map(StreetTypeResponse::new).collect(Collectors.toList()),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
    
    public void delete(Long id){
        streetTypeRepository.delete(findById(id));
    }

    public StreetType findById(Long id){
        return streetTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Street type with id " + id + " does not exist"));
    }

    private StreetType streetTypeRequestToStreetType(StreetTypeRequest request, StreetType streetType) {
        if (streetType == null) {
            streetType = new StreetType();
        }
        streetType.setName(request.getName());
        return streetType;
    }

    public Set<StreetTypeResponse> findAll() {
        return streetTypeRepository.findAll().stream().map(StreetTypeResponse::new).collect(Collectors.toSet());
    }
}
