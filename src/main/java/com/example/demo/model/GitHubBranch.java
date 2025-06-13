package com.example.demo.model;

public class GitHubBranch {
    private String name;
    private String commitSha;
    private boolean isProtected;
    private String protectionUrl;

    public GitHubBranch() {
    }

    public GitHubBranch(String name, String commitSha, boolean isProtected, String protectionUrl) {
        this.name = name;
        this.commitSha = commitSha;
        this.isProtected = isProtected;
        this.protectionUrl = protectionUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommitSha() {
        return commitSha;
    }

    public void setCommitSha(String commitSha) {
        this.commitSha = commitSha;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public void setProtected(boolean isProtected) {
        this.isProtected = isProtected;
    }

    public String getProtectionUrl() {
        return protectionUrl;
    }

    public void setProtectionUrl(String protectionUrl) {
        this.protectionUrl = protectionUrl;
    }
} 