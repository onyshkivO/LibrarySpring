package com.onyshkiv.libraryspring.entity;

public final class Views {
    public interface Id{};
    public interface IdName extends Id{};
    public interface Full extends IdName{};
    public interface FullBook extends IdName{};
    public interface FullAuthor extends IdName{};
    public interface FullPublication extends IdName{};
    public interface FullActiveBook extends IdName{};
    public interface FullUser extends IdName{};



}
