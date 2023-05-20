package gp8.itc.cafe.Controller;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import java.io.File;
import java.io.IOException;

import com.mysql.cj.util.StringUtils;

import gp8.itc.cafe.Controller.DB.RepositoryCafeTable;
import gp8.itc.cafe.Controller.DB.RepositoryDrink;
import gp8.itc.cafe.Controller.DB.RepositoryDrinkCategory;
import gp8.itc.cafe.Controller.DB.RepositoryDrinkSize;
import gp8.itc.cafe.Controller.DB.RepositoryUser;
import gp8.itc.cafe.Controller.DataStructure.CafeTable;
import gp8.itc.cafe.Controller.DataStructure.Drink;
import gp8.itc.cafe.Controller.DataStructure.DrinkCategory;
import gp8.itc.cafe.Controller.DataStructure.DrinkSize;
import gp8.itc.cafe.Controller.DataStructure.User;

import gp8.itc.cafe.Controller.Service.UserService;




@RestController
public class Controller {


    //login
    @GetMapping("/login")
    public Object login() {
        // a constructor of the ModelAndView class in Spring MVC. It is used to create a new instance of ModelAndView with the specified view name.
        // In Spring MVC, ModelAndView is a class that represents a model and view in the MVC pattern. It combines a model object, which holds the data to be displayed, and a view name, which specifies the template or view to render.
        return new ModelAndView("login");
    }
    @Autowired
    private UserService userService;

    @RequestMapping(method=RequestMethod.POST, value="/login")
    @ResponseBody
    public Object processLoginForm(@ModelAttribute("User") User login) {
        String username = login.getUsername();
        String password = login.getPassword();
        String type = login.getType();
        // User user = repositoryLogin.getUserByEmail(login.getEmail());
        if (userService.loginTest(username, password, type)) {
            return new ModelAndView("loginSuccess");
        } else {
            return new ModelAndView("redirect:/login");
        }
    }


    //User
    @Autowired
    private RepositoryUser addCashierRepos;
    @GetMapping("/addCashier")
    public Object addCashier() {
        return new ModelAndView("addCashier");
    }

    @PostMapping("/addCashier")
    @ResponseBody
    public Object processAddCashierForm(@ModelAttribute("User") User addCashier) {
        addCashierRepos.save(addCashier);
        return new RedirectView("/addCashier");  
    }

    //delete user
    @Autowired
    RepositoryUser userRepository;
    @GetMapping("/user/delete/{id}")
    public Object deleteUser(@PathVariable Integer id) {
        //User user = userRepository.findById(id).get();
        userRepository.deleteById(id);
        return new RedirectView("/adminDashboard");
    }
    //edit user
    @GetMapping("/user/edit/{id}")
    public Object editUser(@PathVariable Integer id, Model model) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        return new ModelAndView("/editUser");
    }

    @PostMapping("/user/{id}")
    public Object userUpdated(@PathVariable Integer id,
                            @RequestParam("username") String usernom,
                            @RequestParam("password") String passmot) {
        User user = userRepository.findById(id).get();
    
        user.setUsername(usernom);
        user.setPassword(passmot);

        userRepository.save(user);

        return new RedirectView("/adminDashboard");
    }


    //admin dashboard
    @Autowired
    private RepositoryDrink repositoryDrink;
    @Autowired
    private RepositoryUser repositoryUser;
    @Autowired
    private RepositoryDrinkCategory repositoryDrinkCategory;
    

    // @GetMapping("/testThemeLeaf")
    // public ModelAndView adminDashboard() {
    //     List<Drink> drinks = repositoryDrink.findAll(); 
    //     ModelAndView modelAndView = new ModelAndView("/testThemeLeaf");
    //     modelAndView.addObject("drinks", drinks); // Add the drinks to the model
    //     return modelAndView;
    // }

    @GetMapping("/adminDashboard")
    public ModelAndView adminDashboard1() {
        // Retrieve drinks
        List<Drink> drinks = repositoryDrink.findAll();
    
        // Retrieve users
        List<User> users = repositoryUser.findAll();

        List<DrinkCategory> drinkCategories = repositoryDrinkCategory.findAll();
    
        // Create a class to hold both drinks and users
        class DashboardData {
            private List<Drink> drinks;
            private List<User> users;
            private List<DrinkCategory> drinkCategories;
    
            public DashboardData(List<Drink> drinks, List<User> users, List<DrinkCategory> drinkCategories) {
                this.drinks = drinks;
                this.users = users;
                this.drinkCategories = drinkCategories;
            }
    
            public List<Drink> getDrinks() {
                return drinks;
            }
    
            public List<User> getUsers() {
                return users;
            }

            public List<DrinkCategory> getDrinkCategories() {
                return drinkCategories;
            }
        }
    
        // Create an instance of DashboardData and pass drinks and users to it
        DashboardData dashboardData = new DashboardData(drinks, users, drinkCategories);
    
        // Create a ModelAndView object and add the dashboardData object to it
        ModelAndView modelAndView = new ModelAndView("/adminDashboardTest");
        modelAndView.addObject("dashboardData", dashboardData);
    
        return modelAndView;
    }
    

    //table
    @Autowired
    private RepositoryCafeTable repositoryCafeTable;

    @GetMapping("/index")
    public Object test() {
        return new ModelAndView("index");
    }

    @RequestMapping(method=RequestMethod.POST, value="/index")
    @ResponseBody
    public Object processLoginForm(@ModelAttribute("CafeTable") CafeTable cafeTable) {
        //System.out.println(cafeTable.getTableNumber());
        repositoryCafeTable.save(cafeTable);
        return new RedirectView("/index");  
    }

    //drink

    //add drink
    @Autowired
    RepositoryDrink drinkRepository;
    @Autowired
    RepositoryDrinkSize drinkSizeRepository;
    @Autowired
    RepositoryDrinkCategory drinkCategoryRepository;

    @GetMapping("/addDrink")
    public Object addDrink() {
        return new ModelAndView("addDrink");
    }

    @PostMapping("/addDrink")
    @ResponseBody
    // MultipartFile is a class in Spring Framework that represents a file that has been uploaded via a form in a web application. It is typically used for handling file uploads in Spring applications
    public Object processAddDrinkForm(@RequestParam("drinkName") String drinkNom, @RequestParam("drinkPrice") double drinkPrix, @RequestParam("drinkNote") String note, @RequestParam("categoryName") String categoryNom, @RequestParam("file") MultipartFile limage) {

        Drink drinkName = new Drink();
        drinkName.setDrinkName(drinkNom);
        // the .getOriginalFileName extract image name
        String fileName = limage.getOriginalFilename();
        //if fileName is "C:/path/to/my_image.jpg", then cleanFileName will contain "my_image.jpg", which is the extracted filename from the file path.
        String cleanFileName = new File(fileName).getName();
        if(cleanFileName.contains("..")){
            System.out.println("not a valid file");
        }
        //encode from imagefile to string
        try {
            drinkName.setImage(Base64.getEncoder().encodeToString(limage.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DrinkSize drinkSize = new DrinkSize();
        drinkSize.setPrice(drinkPrix);

        DrinkCategory drinkCategory = new DrinkCategory();
        drinkCategory.setDescription(note);
        drinkCategory.setName(categoryNom);

        // drinkRepository.save(drinkName);
        drinkSizeRepository.save(drinkSize);
        drinkCategoryRepository.save(drinkCategory);
        
        //insert foreign key
        drinkName.setCategory_id(drinkCategory);
        // drinkRepository.save(drinkName);

        drinkName.setSizeId(drinkSize);
        drinkRepository.save(drinkName);

        return new RedirectView("/addDrink");  
    }

    //delete drink
    @GetMapping("/drink/delete/{id}")
    public Object deleteDrink(@PathVariable Integer id) {
        Drink drink = drinkRepository.findById(id).get();
        int cateID = drink.getCategory_id().getDrink_categoryId();
        int sizeID = drink.getSizeId().getDrink_sizeId();
        
        drinkRepository.deleteById(id);
        drinkCategoryRepository.deleteById(cateID);
        drinkSizeRepository.deleteById(sizeID);

        return new RedirectView("/adminDashboard");
    }

    //edit drink

    //go to drink
    // @GetMapping("/drinkDE")
    // public Object drinkCategory(Model model) {
    //     model.addAttribute("drinks", drinkRepository.findAll());
    //     return new ModelAndView("drinkDE");
    // }

    //then to edit
    @GetMapping("/drink/edit/{id}")
    public Object editDrink(@PathVariable Integer id, Model model) {
        Drink drink = drinkRepository.findById(id).get();
        model.addAttribute("drink", drink);
        return new ModelAndView("/editDrink");
    }

    @PostMapping("/drink/{id}")
    public Object drinkUpdated(@PathVariable Integer id,
                            @RequestParam("drinkName") String drinkNom, 
                            @RequestParam("drinkPrice") double drinkPrix, 
                            @RequestParam("drinkNote") String note) {
        Drink drink = drinkRepository.findById(id).get();
        int cateID = drink.getCategory_id().getDrink_categoryId();
        int sizeID = drink.getSizeId().getDrink_sizeId();

        DrinkCategory drinkCategory = drinkCategoryRepository.findById(cateID).get();
        DrinkSize drinkSize = drinkSizeRepository.findById(sizeID).get();
    
        drink.setDrinkName(drinkNom);
        drinkSize.setPrice(drinkPrix);
        drinkCategory.setDescription(note);

        drinkRepository.save(drink);
        drinkCategoryRepository.save(drinkCategory);
        drinkSizeRepository.save(drinkSize);

        return new RedirectView("/drinkDE");
    }
    @GetMapping(path = "/new_cashier")
    public Object newCashier(){
        return new ModelAndView("newCashier");
    }

    @GetMapping(path = "/new_drink")
    public Object newDrink(){
        return new ModelAndView("newDrink");
    }

    @GetMapping(path = "/new_food")
    public Object newFood(){
        return new ModelAndView("newFood");
    }

     @GetMapping(path = "/new_category")
    public Object newCategory(){
        return new ModelAndView("newCategory");
    }

     @GetMapping(path = "/order_histories")
    public Object orderHistories(){
        return new ModelAndView("orderHistories");
    }

     @GetMapping(path = "/manage_table")
    public Object manageTable(){
        return new ModelAndView("manageTable");
    }
}

