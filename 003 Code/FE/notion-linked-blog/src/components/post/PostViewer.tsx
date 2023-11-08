import dynamic from "next/dynamic";

import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import styled from "styled-components";
import {Button, Modal, Space, Typography} from "antd";
import {useState} from "react";
import {requestDeletePostAPI} from "@/apis/post";
import {useRouter} from "next/router";
import {useSelector} from "react-redux";
import Link from "next/link";
import {RootState} from "@/redux/store";
import {UserState} from "@/redux/userSlice";
import CommentContainer from "./comment/CommentContainer";

const MDPreview = dynamic(
	() => import("@uiw/react-markdown-preview").then(mod => mod.default),
	{ssr: false},
);

const StyledMDPreview = styled(MDPreview)`
  background-color: transparent !important;
  margin-top: 80px;
  width: 100%;
  height: 100%;
`;

const StyledContainer = styled.div`
  width: 768px;

  @media screen and (max-width: 768px) {
    width: 100vw;
    padding: 0 16px;
  }

  margin: 88px 567.5px 64px;
`;

const StyledTop = styled.div`
`;

const StyledButtonDiv = styled.div`
  & > Button {
    margin: 10px;
  }
`;

export default function PostViewer({post, isDark}) {
	const router = useRouter();
	const [isModalOpen, setIsModalOpen] = useState(false);
	const {user} = useSelector<RootState, UserState>(state => state.user);

	const showModal = () => {
		setIsModalOpen(true);
	};
	const handleOk = async () => {
		setIsModalOpen(false);
		try {
			await requestDeletePostAPI(post.postId);
			await router.replace("/");
		} catch (e) {
			console.log("포스트 삭제 요청 관련 에러", e);
		}
	};

	const handleCancel = () => {
		setIsModalOpen(false);
	};

	return (
		<Space direction="vertical" align="center" style={{height: "100vh"}}>
			<StyledContainer data-color-mode={isDark ? "dark" : "light"} >
				<StyledTop>
					<Typography.Title level={1}>{post.title}</Typography.Title>
					<Typography.Text>{post.author} · {post.createdAt}</Typography.Text>
				</StyledTop>
				<div className="wmde-markdown-var" />
				<StyledMDPreview source={post.content} data-color-mode={isDark ? "dark" : "light"} />
				{user?.username === post.author &&
					<StyledButtonDiv>
						<Link href={{
							pathname: `/write/${post.postId}`,
							query: {
								postId: post.postId,
								title: post.title,
								content: post.content,
								author: post.author,
								requestThumbnailLink: post.requestThumbnailLink,
								description: post.description,
							},
						}} as={`/write/${post.postId}`}>
							<Button type="primary">
								수정하기
							</Button>
						</Link>
						<Button type="primary" danger onClick={showModal}>
							삭제하기
						</Button>
						<Modal title="포스트 삭제" open={isModalOpen} onOk={handleOk} onCancel={handleCancel} okText="확인" cancelText="취소">
							<p>포스트를 정말 삭제하시겠습니까?</p>
						</Modal>
					</StyledButtonDiv>
				}
			</StyledContainer>
			<CommentContainer postId={post.postId} />
		</Space >
	);
}
