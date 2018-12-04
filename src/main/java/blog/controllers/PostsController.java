package blog.controllers;


import blog.forms.EditForm;
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

    @RequestMapping("/index")
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
    public String createForm(EditForm editForm){
        return "posts/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(@Valid EditForm editForm, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            notifyService.addErrorMessage("Please fill the form correctly!");
            return "posts/create";
        }

        //TODO

        Post newPost = new Post();
        newPost.setTitle(editForm.getTitle());
        newPost.setBody(editForm.getBody());
        newPost.setAuthor(userService.findById((long) 1));
        postService.create(newPost);

        notifyService.addInfoMessage("Post created");

        return "redirect:/posts/index";
    }

    //------------------------------------------------------------------------------------------------------------------
    // EDIT ------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editForm(@PathVariable("id") Long id, EditForm editForm){
        Post post = postService.findById(id);
        if (post == null) {
            notifyService.addErrorMessage("Cannot find post #" + id);
            return "redirect:/";
        }
        editForm.setTitle(post.getTitle());
        editForm.setBody(post.getBody());
        return "/posts/edit";
    }

    @RequestMapping(value = "/edit{id}", method = RequestMethod.POST)
    public String edit(@Valid EditForm editForm,
                    @PathVariable("id") Long id,
                    BindingResult bindingResult){

        Post post = postService.findById(id);
        if(post == null){
            notifyService.addErrorMessage("Cannot find post #" + id);
            return "redirect:/posts/index";
        }

        if (bindingResult.hasErrors()) {
            notifyService.addErrorMessage("Please fill the form correctly!");
            return "posts/edit";
        }

        post.setTitle(editForm.getTitle());
        post.setBody(editForm.getBody());
        postService.edit(post);

        return "posts/index";
    }

    //------------------------------------------------------------------------------------------------------------------
    // DELETE ----------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteForm(@PathVariable("id") Long id){

        return "posts/delete";
    }
}