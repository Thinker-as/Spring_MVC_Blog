package blog.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class EditForm {

    @NotNull
    @Size(min = 1, max = 300, message = "Title size should be in the range [1...300]")
    private String title;

    private String body;
}
