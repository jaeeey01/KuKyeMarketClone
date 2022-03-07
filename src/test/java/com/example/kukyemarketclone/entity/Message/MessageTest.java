package com.example.kukyemarketclone.entity.Message;

import org.junit.jupiter.api.Test;

import static com.example.kukyemarketclone.factory.entity.MessageFactory.createMessage;
import static org.assertj.core.api.Assertions.assertThat;

class MessageTest {

    @Test
    void deletedBySenderTest(){
        //given
        Message message = createMessage();

        //when
        message.deleteBySender();

        //then
        assertThat(message.isDeletedBySender()).isTrue();
    }

    @Test
    void deletedByReceiverTest(){
        //given
        Message message = createMessage();

        //when
        message.deleteByReceiver();

        //then
        assertThat(message.isDeletedByReceiver()).isTrue();
    }

    @Test
    void isNotDeletableTest(){
        //given
        Message message = createMessage();

        //when
        boolean deletable = message.isDeletable();

        //then
        assertThat(deletable).isFalse();
    }

    @Test
    void isDeletableTest(){
        //given
        Message message = createMessage();
        message.deleteByReceiver();
        message.deleteBySender();

        //when
        boolean deletable = message.isDeletable();

        //then
        assertThat(deletable).isTrue();
    }

}