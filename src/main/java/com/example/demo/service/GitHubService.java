package com.example.demo.service;

import com.example.demo.model.GitHubPR;
import com.example.demo.model.GitHubBranch;
import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class GitHubService {

    @Value("${github.token:}")
    private String githubToken;

    private final WebClient webClient;

    public GitHubService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
    }

    /**
     * 使用GitHub API获取指定仓库的PR信息
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @param prNumber PR编号
     * @return PR信息
     */
    public GitHubPR getPullRequest(String owner, String repo, int prNumber) {
        try {
            GitHub github;
            if (githubToken != null && !githubToken.isEmpty()) {
                github = new GitHubBuilder().withOAuthToken(githubToken).build();
            } else {
                github = GitHub.connectAnonymously();
            }

            GHRepository repository = github.getRepository(owner + "/" + repo);
            GHPullRequest pullRequest = repository.getPullRequest(prNumber);

            GitHubPR pr = new GitHubPR(
                    pullRequest.getNumber(),
                    pullRequest.getTitle(),
                    pullRequest.getState().name(),
                    pullRequest.getHtmlUrl().toString(),
                    pullRequest.getUser().getLogin()
            );

            pr.setBody(pullRequest.getBody());
            pr.setCreatedAt(pullRequest.getCreatedAt().toString());
            pr.setUpdatedAt(pullRequest.getUpdatedAt().toString());
            
            if (pullRequest.getClosedAt() != null) {
                pr.setClosedAt(pullRequest.getClosedAt().toString());
            }
            
            if (pullRequest.getMergedAt() != null) {
                pr.setMergedAt(pullRequest.getMergedAt().toString());
            }
            
            pr.setComments(pullRequest.getComments());
            pr.setCommits(pullRequest.getCommits());
            pr.setAdditions(pullRequest.getAdditions());
            pr.setDeletions(pullRequest.getDeletions());
            pr.setChangedFiles(pullRequest.getChangedFiles());

            return pr;
        } catch (IOException e) {
            throw new RuntimeException("获取GitHub PR信息失败", e);
        }
    }

    /**
     * 使用WebClient获取指定仓库的PR列表
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @param state PR状态 (open, closed, all)
     * @param limit 返回数量限制
     * @return PR列表
     */
    public Mono<List<GitHubPR>> listPullRequests(String owner, String repo, String state, int limit) {
        String url = "/repos/" + owner + "/" + repo + "/pulls";
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(url)
                        .queryParam("state", state)
                        .queryParam("per_page", limit)
                        .build())
                .headers(headers -> {
                    if (githubToken != null && !githubToken.isEmpty()) {
                        headers.setBearerAuth(githubToken);
                    }
                })
                .retrieve()
                .bodyToFlux(java.util.Map.class)
                .collectList()
                .map(list -> {
                    List<GitHubPR> prs = new ArrayList<>();
                    for (java.util.Map prMap : list) {
                        GitHubPR pr = new GitHubPR();
                        // number/title/state/html_url/user.login
                        Object number = prMap.get("number");
                        if (number != null) pr.setId(Long.valueOf(number.toString()));
                        pr.setTitle((String) prMap.get("title"));
                        pr.setState((String) prMap.get("state"));
                        pr.setHtmlUrl((String) prMap.get("html_url"));
                        Object userObj = prMap.get("user");
                        if (userObj instanceof java.util.Map) {
                            Object login = ((java.util.Map) userObj).get("login");
                            if (login != null) pr.setUser(login.toString());
                        }
                        pr.setBody((String) prMap.get("body"));
                        pr.setCreatedAt(prMap.get("created_at") != null ? prMap.get("created_at").toString() : null);
                        pr.setUpdatedAt(prMap.get("updated_at") != null ? prMap.get("updated_at").toString() : null);
                        pr.setClosedAt(prMap.get("closed_at") != null ? prMap.get("closed_at").toString() : null);
                        pr.setMergedAt(prMap.get("merged_at") != null ? prMap.get("merged_at").toString() : null);
                        // comments/commits/additions/deletions/changed_files
                        Object comments = prMap.get("comments");
                        if (comments != null) pr.setComments(Integer.parseInt(comments.toString()));
                        Object commits = prMap.get("commits");
                        if (commits != null) pr.setCommits(Integer.parseInt(commits.toString()));
                        Object additions = prMap.get("additions");
                        if (additions != null) pr.setAdditions(Integer.parseInt(additions.toString()));
                        Object deletions = prMap.get("deletions");
                        if (deletions != null) pr.setDeletions(Integer.parseInt(deletions.toString()));
                        Object changedFiles = prMap.get("changed_files");
                        if (changedFiles != null) pr.setChangedFiles(Integer.parseInt(changedFiles.toString()));
                        prs.add(pr);
                    }
                    return prs;
                });
    }

    /**
     * 获取指定仓库的分支列表
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @return 分支列表
     */
    public List<GitHubBranch> getBranches(String owner, String repo) {
        try {
            GitHub github;
            if (githubToken != null && !githubToken.isEmpty()) {
                github = new GitHubBuilder().withOAuthToken(githubToken).build();
            } else {
                github = GitHub.connectAnonymously();
            }

            GHRepository repository = github.getRepository(owner + "/" + repo);
            List<GHBranch> branches = repository.getBranches().values().stream().collect(Collectors.toList());
            
            return branches.stream().map(branch -> {
                GitHubBranch gitHubBranch = new GitHubBranch();
                gitHubBranch.setName(branch.getName());
                gitHubBranch.setCommitSha(branch.getSHA1());
                gitHubBranch.setProtected(branch.isProtected());
                gitHubBranch.setProtectionUrl(branch.getProtectionUrl());
                return gitHubBranch;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("获取GitHub分支信息失败", e);
        }
    }
}