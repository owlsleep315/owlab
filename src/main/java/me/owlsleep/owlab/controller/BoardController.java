package me.owlsleep.owlab.controller;

import jakarta.servlet.http.HttpSession;
import me.owlsleep.owlab.dto.CommentDto;
import me.owlsleep.owlab.dto.PostDto;
import me.owlsleep.owlab.entity.ReactionType;
import me.owlsleep.owlab.entity.User;
import me.owlsleep.owlab.service.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public String boardList(@RequestParam(required = false) String category,
                            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                            Model model) {
        Page<PostDto> posts = boardService.getAllPosts(category, pageable);
        model.addAttribute("posts", posts);
        model.addAttribute("category", category);
        return "board/list";
    }

    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Long id, Model model, HttpSession session) {
        boardService.incrementViewCount(id);
        User loginUser = (User) session.getAttribute("loginUser");
        Long userId = loginUser != null ? loginUser.getId() : null;
        PostDto post = boardService.getPostById(id, userId);

        model.addAttribute("post", post);
        model.addAttribute("comments", boardService.getCommentsByPostId(id));
        model.addAttribute("loginUser", loginUser);

        return "board/view";
    }

    @GetMapping("/write")
    public String writeForm(Model model, HttpSession session) {
        model.addAttribute("postDto", new PostDto());
        model.addAttribute("loginUser", session.getAttribute("loginUser"));
        return "board/write";
    }

    @PostMapping("/write")
    public String writePost(@ModelAttribute PostDto postDto, HttpSession session) {
        if (postDto.getCategory().equals("공지사항") && !((User)session.getAttribute("loginUser")).getRole().equals("ADMIN")) {
            throw new IllegalArgumentException("공지사항은 관리자만 등록할 수 있습니다.");
        }

        String author = ((User)session.getAttribute("loginUser")).getUsername();
        postDto.setAuthor(author);
        boardService.writePost(postDto);
        return "redirect:/board";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, HttpSession session) {
        User user = (User)session.getAttribute("loginUser");
        PostDto post = boardService.getPostById(id, user.getId());
        if (!post.getAuthor().equals(user.getUsername()) && !user.isAdmin()) {
            return "redirect:/board";
        }
        model.addAttribute("postDto", post);
        return "board/write";
    }

    @PostMapping("/edit")
    public String editPost(@ModelAttribute PostDto postDto) {
        boardService.updatePost(postDto);
        return "redirect:/board/view/" + postDto.getId();
    }

    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        boardService.deletePost(id);
        return "redirect:/board";
    }

    @PostMapping("/{id}/reaction")
    @ResponseBody
    public Map<String, Object> reactToPost(@PathVariable Long id, @RequestParam String type, HttpSession session) {
        User user = (User)session.getAttribute("loginUser");
        return boardService.reactToPost(id, user.getId(), ReactionType.valueOf(type));
    }

    @PostMapping("/comment")
    public String addComment(@ModelAttribute CommentDto commentDto, HttpSession session) {
        String author = ((User)session.getAttribute("loginUser")).getUsername();
        commentDto.setAuthor(author);
        boardService.addComment(commentDto);
        return "redirect:/board/view/" + commentDto.getPostId();
    }

    @PostMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId, HttpSession session) {
        String author = ((User)session.getAttribute("loginUser")).getName();
        boardService.deleteComment(id, author);
        return "redirect:/board/view/" + postId;
    }
}