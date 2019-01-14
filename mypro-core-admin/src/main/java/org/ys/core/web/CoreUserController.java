package org.ys.core.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ys.core.model.CoreUser;

@RestController
@RequestMapping("coreUserController")
public class CoreUserController {

    @GetMapping("/coreUser/{coreUserId}")
    public CoreUser coreUser(@PathVariable("coreUserId") String coreUserId){
        CoreUser coreUser = new CoreUser();
        coreUser.setCoreUserId(Long.parseLong(coreUserId));
        return coreUser;
    }
    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable String id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "product id : " + id;
    }

    @GetMapping("/order/{id}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getOrder(@PathVariable String id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "order id : " + id;
    }
}
