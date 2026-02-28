package com.example.demo.controller;

import com.example.demo.model.GitHubPR;
import com.example.demo.model.GitHubBranch;
import com.example.demo.service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    @Autowired
    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * 获取指定仓库的PR信息
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @param prNumber PR编号
     * @return PR信息
     */
    @GetMapping("/repos/{owner}/{repo}/pulls/{prNumber}")
    public ResponseEntity<GitHubPR> getPullRequest(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable int prNumber) {
        
        GitHubPR pr = gitHubService.getPullRequest(owner, repo, prNumber);
        return ResponseEntity.ok(pr);
    }

    /**
     * 获取指定仓库的PR列表
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @param state PR状态 (open, closed, all)，默认为open
     * @param limit 返回数量限制，默认为10
     * @return PR列表
     */
    @GetMapping("/repos/{owner}/{repo}/pulls")
    public Mono<ResponseEntity<List<GitHubPR>>> listPullRequests(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "open") String state,
            @RequestParam(defaultValue = "10") int limit) {
        
        return gitHubService.listPullRequests(owner, repo, state, limit)
                .map(ResponseEntity::ok);
    }

    /**
     * 获取指定仓库的分支列表
     * @param owner 仓库所有者
     * @param repo 仓库名称
     * @return 分支列表
     */
    @GetMapping("/repos/{owner}/{repo}/branches")
    public ResponseEntity<List<GitHubBranch>> getBranches(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        List<GitHubBranch> branches = gitHubService.getBranches(owner, repo);
        return ResponseEntity.ok(branches);
    }
}