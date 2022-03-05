package com.example.kukyemarketclone.dto.sign;

import com.example.kukyemarketclone.entity.member.Member;
import com.example.kukyemarketclone.entity.member.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel(value = "회원가입 요청")//swagger 모델로 사용하기 위한 부가정보 입력
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    //형식 위반시 MethodArgumentNotValidException 예외 발생

    //@ApiModelProperty swagger 각각의 프로퍼티에 대한 부가 정보
    @ApiModelProperty(value = "이메일",notes = "이메일을 입력해 주세요", required = true, example = "member@member.com")
    @Email(message = "{signUpRequest.email.email}")
    @NotBlank(message = "{signUpRequest.email.notBlank}")
    private String email;

    @ApiModelProperty(value = "비밀번호",notes = "비밀번호는 최소 8자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다", required = true , example = "123456a!")
    @NotBlank(message = "{signUpRequest.password.notBlank}")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "{signUpRequest.password.pattern}")
    private String password;

    @ApiModelProperty(value = "사용자 이름", notes = "사용자 이름은 한글 또는 알파벳으로 입력해주세요", required = true, example = "유재현")
    @NotBlank(message = "{signUpRequest.username.notBlank}")
    @Size(min = 2, message = "{signUpRequest.username.size}")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "{signUpRequest.username.pattern}")
    private String username;

    @ApiModelProperty(value = "닉네임", notes = "닉네임은 한글 또는 알파벳만 입력해주세요", required = true, example = "가나다라")
    @NotBlank(message = "{signUpRequest.nickname.notBlank}")
    @Size(min = 2, message="{signUpRequest.nickname.size}")
    @Pattern(regexp = "^[A-Za-z가-힣]+$", message = "{signUpRequest.nickname.pattern}")
    private String nickname;

    public static Member toEntity(SignUpRequest req, Role role, PasswordEncoder encoder){
        return new Member(req.email,encoder.encode(req.password),req.username, req.nickname, List.of(role));
    }
}
