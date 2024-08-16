package by.sakuuj.blogsite.article.controller;

import lombok.Builder;

@Builder
public record RequestedPage(int number, int size) {}