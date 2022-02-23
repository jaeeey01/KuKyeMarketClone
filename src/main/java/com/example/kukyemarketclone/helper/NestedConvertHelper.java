package com.example.kukyemarketclone.helper;

import com.example.kukyemarketclone.exception.CannotConvertNestedStructureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NestedConvertHelper<K,E,D> {
    //3개의 제너릭 파라미터 받음
    // K: 엔티티의 key 타입,
    // E:엔티티의 타입
    // D: 엔티티가 변환된 DTO의 타입

    private List<E> entities;//엔티티 목록, 정렬된 엔티티 목록을 전달 받으면 ,각 엔티티가 자식 엔티티를 나타내는 계층형구조의 DTO 목록으로 변환 시켜줌
    private Function<E,D> toDto;//엔티티를 DTO로 변환해주는 Function
    private Function<E,E> getParent;//엔티티의 부모 엔티티를 반환해주는 Function
    private Function<E,K> getKey;//엔티티의 key(id)를 반환해주는 Function
    private Function<D,List<D>> getChildren; //Dto의 children 리스트를 반환해주는 Function

    public static <K,E,D> NestedConvertHelper newInstance(List<E> entities, Function<E,D>toDto, Function<E,E> getParent,Function<E,K>getKey, Function<D,List<D>>getChildren){
        //스태틱 메소드 이기 떄문에 메소드 레벨에 제너릭 타입 파라미터 지정
        //전달받은 함수를 이용합 타입 추론에 의해 각각의 제너릭 타입이 추론 될 것
        return new NestedConvertHelper<K,E,D>(entities, toDto, getParent, getKey, getChildren);
    }

    private NestedConvertHelper(List<E> entities, Function<E, D> toDto, Function<E, E> getParent, Function<E, K> getKey, Function<D, List<D>> getChildren){
        this.entities = entities;
        this.toDto = toDto;
        this.getParent = getParent;
        this.getKey = getKey;
        this.getChildren = getChildren;
    }

    /* convert()
    * 계층형 변환 작업 수행
    * 단, entities에 대해 사전 조건 필요
    * = entities를 순차적 탐색을 하면서 어떤 카테고리의 부모 카테고리 id는 반드시 해당 카테고리보다 앞서야함
    *
    * 만약 그렇지 않으면 NPE 발생 + CannotConvertNestedStructureException 예외 발생
    *
    * */

    public List<D> convert(){
        try{
            return convertInternal();
        }catch(NullPointerException e){
            throw new CannotConvertNestedStructureException(e.getMessage());
        }
    }

    //roots에는 자식 엔티티가 없는 루트 엔티티가 담기게 됨, 최종 반환 값은 roots
    //roots에 담긴 DTO의 children들은 자신의 자식들을 담고 있음 = MAP 이용

    private List<D> convertInternal(){
        Map<K,D> map = new HashMap<>();
        List<D> roots = new ArrayList<>();

        for(E e : entities){//사전 조건에 의해 정렬된 entities를 순차적 탐색
            D dto = toDto(e);
            map.put(getKey(e),dto);//탐색된 엔티티를 DTO로 변환 -> map 넣어줌 *이미 탐색된 부모 엔티티의 DTO는 어떤 자식의 엔티티를 탐색할 떄 반드시 Map에 담겨 있어야함 그렇지 않으면 NPE
            if(hasParent(e)){//부모가 있다면 map에서 부모 DTO찾음, 부모 DTO의 children으로 지금 탐색하는 엔티티의 DTO를 넣어줌
                E parent = getParent(e);
                K parentKey = getKey(parent);
                D parentDto = map.get(parentKey);
                getChildren(parentDto).add(dto);
            }else{
                roots.add(dto);//부모가 없다면 루트 엔티티 해당 DTO를 roots에 넣어줌
            }
        }
        return roots;
    }

    private boolean hasParent(E e){
        return getParent(e) != null;
    }

    private E getParent(E e){
        return getParent.apply(e);
    }

    private D toDto(E e){
        return toDto.apply(e);
    }

    private K getKey(E e){
        return getKey.apply(e);
    }

    private List<D> getChildren(D d){
        return getChildren.apply(d);
    }
}
