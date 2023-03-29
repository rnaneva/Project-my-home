package bg.softuni.myhome.web;

import bg.softuni.myhome.model.dto.*;
import bg.softuni.myhome.model.view.OfferDetailsView;
import bg.softuni.myhome.model.view.OfferView;
import bg.softuni.myhome.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static bg.softuni.myhome.staticVariables.StaticVariables.BINDING_RESULT;

@Controller
@RequestMapping("/offers")
public class OffersController {


    private final OfferService offerService;
    private final CategoryService categoryService;
    private final CityService cityService;
    private final SearchService searchService;
    private final RequestService requestService;

    public OffersController(OfferService offerService, CategoryService categoryService,
                            CityService cityService, SearchService searchService, RequestService requestService) {
        this.offerService = offerService;
        this.categoryService = categoryService;
        this.cityService = cityService;
        this.searchService = searchService;
        this.requestService = requestService;
    }


    @GetMapping("/rent")
    public String getRent(Model model) {
        List<OfferView> offers = offerService.allRentProperties();
        List<String> allCategoryNames = categoryService.getAllCategoryNames();
        List<String> allCityNames = cityService.getAllCityNames();
        model.addAttribute("rentOffers", offers);
        model.addAttribute("categories", allCategoryNames);
        model.addAttribute("cities", allCityNames);

        return "rent-offers";
    }

//todo pageable
    @PostMapping("/rent")
    public String postRentSearch(@Valid @ModelAttribute("searchFormDTO") SearchFormDTO searchFormDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("searchFormDTO", searchFormDTO)
                    .addFlashAttribute(BINDING_RESULT + "searchFormDTO", bindingResult);

            return "redirect:rent";
        }

        String visibleId = searchService.saveSearchCriteria(searchFormDTO);

        return "redirect:/search/" + visibleId;
    }


    @GetMapping("/sale")
    public String getSale(Model model) {
        List<OfferView> offers = offerService.allSaleProperties();
        List<String> allCategoryNames = categoryService.getAllCategoryNames();
        List<String> allCityNames = cityService.getAllCityNames();
        model.addAttribute("saleOffers", offers);
        model.addAttribute("categories", allCategoryNames);
        model.addAttribute("cities", allCityNames);

        return "sale-offers";
    }

    //todo pageable
    @PostMapping("/sale")
    public String postSaleSearch(@Valid @ModelAttribute("searchFormDTO") SearchFormDTO searchFormDTO,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("searchFormDTO", searchFormDTO)
                    .addFlashAttribute(BINDING_RESULT + "searchFormDTO", bindingResult);

            return "redirect:sale";
        }

        String visibleId = searchService.saveSearchCriteria(searchFormDTO);

        return "redirect:/search/" + visibleId;
    }

    @GetMapping("/{visibleId}")
    public String getOfferDetails(@PathVariable String visibleId, Model model){
        OfferDetailsView detailedOffer = offerService.findDetailedOfferByVisibleId(visibleId);
        model.addAttribute(detailedOffer);


        return "offer-details";
    }


    @PostMapping("/{visibleId}")
    public String postRequest(@PathVariable String visibleId, @Valid RequestDTO requestDTO,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,
                              Model model){

       if(bindingResult.hasErrors()){
           redirectAttributes.addFlashAttribute("requestDTO", requestDTO)
                   .addFlashAttribute(BINDING_RESULT + "requestDTO", bindingResult);

           return "redirect:/offers/" + visibleId;
       }

       requestService.saveRequest(requestDTO, visibleId);

//todo event message for successful submission

        return "redirect:/offers/" + visibleId + "#send-request";
    }





    @ModelAttribute
    public SearchFormDTO searchFormDTO() {
        return new SearchFormDTO();
    }

    @ModelAttribute
    public OfferDetailsView offerDetailsView(){
        return new OfferDetailsView();
    }

    @ModelAttribute
    public RequestDTO requestDTO(){
        return new RequestDTO();
    }

}
