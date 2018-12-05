package blog.controllers;


import blog.models.Post;
import blog.services.NotificationService;
import blog.services.PostService;
import blog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostsController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notifyService;

    //******************************************************************************************************************
    // INDEX ***********************************************************************************************************

    @RequestMapping({"/index", "/"})
    public String allPosts(Model model){
        List<Post> listWithAllPosts = postService.findAll();
        model.addAttribute("allPosts", listWithAllPosts);
        return "posts/index";
    }

    //------------------------------------------------------------------------------------------------------------------
    // VIEW ------------------------------------------------------------------------------------------------------------

    @RequestMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        Post post = postService.findById(id);
        if (post == null) {
            notifyService.addErrorMessage("Cannot find post #" + id);
            return "redirect:/";
        }
        model.addAttribute("post", post);
        return "posts/view";
    }

    //------------------------------------------------------------------------------------------------------------------
    // CREATE ----------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createForm(Post post){
        return "posts/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid Post post, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            notifyService.addErrorMessage("Please fill the form correctly!");
            return "posts/create";
        }

        //TODO

        Post newPost = new Post();
        newPost.setTitle(post.getTitle());
        newPost.setBody(post.getBody());
        newPost.setAuthor(userService.findById((long) 1));
        try {
            postService.create(newPost);
            notifyService.addInfoMessage("Post created");
        } catch (Exception e) {
            notifyService.addErrorMessage(e.getMessage());
        }



        return "redirect:/posts/index";
    }

    //------------------------------------------------------------------------------------------------------------------
    // EDIT ------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editForm(@PathVariable("id") Long id,
                           Post post){
        Post tempPost = postService.findById(id);
        if (post == null) {
            notifyService.addErrorMessage("Cannot find post #" + id);
            return "redirect:/posts/index";
        }
        post.setTitle(tempPost.getTitle());
        post.setBody(tempPost.getBody());
        return "/posts/edit";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
    public String edit(@Valid Post post,
                    @PathVariable("id") Long id,
                    BindingResult bindingResult){

        Post editedPost = postService.findById(id);
        if(post == null){
            notifyService.addErrorMessage("Cannot find post #" + id);
            return "redirect:/posts/index";
        }

        if (bindingResult.hasErrors()) {
            notifyService.addErrorMessage("Please fill the form correctly!");
            return "posts/edit";
        }

        editedPost.setTitle(post.getTitle());
        editedPost.setBody(post.getBody());
        try {
            postService.edit(editedPost);
            notifyService.addInfoMessage("Post edited");
        }catch (Exception ex){
            notifyService.addErrorMessage(ex.getMessage());
        }

        return "redirect:/posts/index";
    }

    //------------------------------------------------------------------------------------------------------------------
    // DELETE ----------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteForm(@PathVariable("id") Long id){

        try {
            postService.deleteById(id);
            notifyService.addInfoMessage("Post deleted");
        } catch (Exception e) {
            notifyService.addErrorMessage(e.getMessage());
        }
        return "redirect:/posts/index";
    }
}