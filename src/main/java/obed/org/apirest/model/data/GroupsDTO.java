package obed.org.apirest.model.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class GroupsDTO {
    private String groupName;
    private Set<String> itemTypes;
}
