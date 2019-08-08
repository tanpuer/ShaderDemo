//
// Created by templechen on 2019/3/15.
//

#include <pthread.h>
#include "looper.h"
#include "../common/native_log.h"

looper::looper() {
    head = nullptr;
    sem_init(&headdataavailable, 0, 0);
    sem_init(&headwriteprotect, 0, 1);
    pthread_attr_t attr;
    pthread_attr_init(&attr);
    pthread_create(&worker_thread, &attr, trampoline, this);
    running = true;
}

looper::~looper() {
    if (running) {
        quit();
    }
}

void looper::postMessage(int what) {
    postMessage(what, 0, 0, nullptr);
}

void looper::postMessage(int what, void *obj) {
    postMessage(what, 0, 0, obj);
}

void looper::postMessage(int what, int arg1, int arg2) {
    postMessage(what, arg1, arg2, nullptr);
}

void looper::postMessage(int what, int arg1, int arg2, void *obj) {
    auto *msg = new LooperMessage();
    msg->what = what;
    msg->arg1 = arg1;
    msg->arg2 = arg2;
    msg->obj = obj;
    msg->quit = false;
    addMessage(msg);
}

void looper::quit() {
    auto *msg = new LooperMessage();
    msg->what = 0;
    msg->arg1 = 0;
    msg->arg2 = 0;
    msg->obj = nullptr;
    msg->quit = true;
    addMessage(msg);
    pthread_join(worker_thread, nullptr);
    sem_destroy(&headwriteprotect);
    sem_destroy(&headdataavailable);
    running = false;
}

void looper::addMessage(looper::LooperMessage *msg) {
    sem_wait(&headwriteprotect);
    if (head == nullptr) {
        head = msg;
    } else {
        LooperMessage *h = head;
        while (h->next != nullptr) {
            h = h->next;
        }
        h->next = msg;
    }
    sem_post(&headwriteprotect);
    sem_post(&headdataavailable);
}

void *looper::trampoline(void *p) {
    ((looper *) p)->loop();
    ((looper *) p)->pthreadExit();
    return nullptr;
}

void looper::loop() {
    while (true) {
        sem_wait(&headdataavailable);
        sem_wait(&headwriteprotect);
        LooperMessage *msg = head;
        if (msg == nullptr) {
            sem_post(&headwriteprotect);
            continue;
        }
        head = msg->next;
        sem_post(&headwriteprotect);
        if (msg->quit) {
            delete msg;
            return;
        }
        handleMessage(msg);
        delete msg;
    }
}

void looper::handleMessage(looper::LooperMessage *msg) {

}

void looper::pthreadExit() {

}
