package com.songlea.demo.proxy.test;

public class TargetImpl implements Target {

    @Override
    public int test(int i) {
        return i + 1;
    }
}
