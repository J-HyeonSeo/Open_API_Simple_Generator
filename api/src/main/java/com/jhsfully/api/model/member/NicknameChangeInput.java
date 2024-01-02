package com.jhsfully.api.model.member;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameChangeInput {
    @NotNull(message = "변경할 닉네임을 2~7자 사이로 입력해주세요.")
    @Size(min = 2, max = 7, message = "변경할 닉네임을 2~7자 사이로 입력해주세요.")
    private String nickname;
}
