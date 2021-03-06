package stupnytskiy.rostyslav.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import stupnytskiy.rostyslav.demo.dto.request.RealtyRequest;
import stupnytskiy.rostyslav.demo.dto.response.RealtyResponse;
import stupnytskiy.rostyslav.demo.entity.*;
import stupnytskiy.rostyslav.demo.repository.RealtyRepository;
import stupnytskiy.rostyslav.demo.tools.FileTool;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RealtyService {

    @Autowired
    private RealtyRepository realtyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private HomeTypeService homeTypeService;

    @Autowired
    private FileTool fileTool;

    public void save (RealtyRequest request) throws IOException {
        realtyRepository.save(realtyRequestToRealty(request,null));
    }

    public void update(RealtyRequest request, Long id) throws IOException {
        realtyRepository.save(realtyRequestToRealty(request, findById(id)));
    }

    public List<RealtyResponse> findAll(){
        return realtyRepository.findAll()
                .stream()
                .map(RealtyResponse::new)
                .collect(Collectors.toList());
    }

    public void delete(Long id){
        realtyRepository.delete(findById(id));
    }

    public Realty findById(Long id){
        return realtyRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Realty with id " + id + " does not exist"));
    }

    private Realty realtyRequestToRealty(RealtyRequest request, Realty realty) throws IOException {
        if(realty == null){
            realty = new Realty();
        }
        String userDir;
        Realtor realtor = userService.findByLogin((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRealtor();
        realty.setName(request.getName());
        realty.setArea(request.getArea());
        if (realtor.getFirm() == null) {
            realty.setRealtor(realtor);
            userDir = "user_" + realtor.getUser().getId();
        }
        else {
            realty.setFirm(realtor.getFirm());
            userDir = "user_" + realtor.getFirm().getUser().getId();
        }
        realty.setBasement(request.getBasement());
        realty.setRent(request.getRent());
        realty.setDescription(request.getDescription());
        realty.setEndDate(request.getEndDate());
        realty.setStartDate(request.getStartDate());
        realty.setPrice(request.getPrice());
        realty.setStage(request.getStage());
        realty.setStagesCount(request.getStagesCount());
        realty.setHomeType(homeTypeService.findById(request.getHomeTypeId()));
        if(request.getMainImage() != null) realty.setMainImage(fileTool.saveRealtyImage(request.getMainImage(), userDir));
        if (request.getImages() != null) {
            for (String image : request.getImages()) {
                realty.getImages().add(fileTool.saveRealtyImage(image , userDir));
            }
        }
        if (realty.getAddress() == null) realty.setAddress(addressService.addressRequestToAddress(request.getAddress(), null));
        else realty.setAddress(addressService.addressRequestToAddress(request.getAddress(),realty.getAddress()));
        return realty;
    }
}
