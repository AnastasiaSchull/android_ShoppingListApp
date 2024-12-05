package com.example.android_shoppinglistapp.data;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shopping implements Serializable {
	private Integer id;
	private String name;   // название покупки
}
