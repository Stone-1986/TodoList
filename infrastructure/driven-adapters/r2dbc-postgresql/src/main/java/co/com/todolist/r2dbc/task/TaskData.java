package co.com.todolist.r2dbc.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task", schema = "public")
public class TaskData {
    @Id
    @Column("id")
    private String id;
    @Column("titulo")
    private String title;
    @Column("descripcion")
    private String description;
    @Column("estado")
    private Boolean  completed;
}
