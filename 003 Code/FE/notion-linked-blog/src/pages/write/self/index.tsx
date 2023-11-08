import "@uiw/react-md-editor/markdown-editor.css";
import "@uiw/react-markdown-preview/markdown.css";
import PostWriteSetting from "@/components/post/PostWriteSetting";
import {useState} from "react";
import Link from "next/link";
import dynamic from "next/dynamic";
import handleInput from "@/components/auth/common";
import {
	ButtonSpace,
	WriteDiv,
	StyledInput,
} from "@/components/post/Post";
import {Button, Layout} from "antd";
import styled from "styled-components";

const PostEditor = dynamic(
	() => import("@/components/post/PostWrite"),
	{ssr: false},
);

const StyledButtonDiv = styled.div`
& > Button {
	margin: 10px;
}
`;

export default function WritingSelf({isDark}) {
	const [title, onChangeTitle] = handleInput("");
	const [content, setContent] = useState("**내용을 작성해주세요**");
	const [isDoneWrite, setIsDoneWrite] = useState(false);

	const isDoneWritePost = () => {
		setIsDoneWrite(prev => !prev);
	};

	const editContent = contents => {
		setContent(contents);
	};

	return (
		<Layout style={{height: "100vh"}}>
			{isDoneWrite ?
				<PostWriteSetting
					title={title} content={content} isDoneWritePost={isDoneWritePost} isDark={isDark} /> :
				<WriteDiv>
					<StyledInput bordered={false} value={title} placeholder="제목을 입력하세요" onChange={onChangeTitle} />
					<PostEditor content={content} editContent={editContent} isDark={isDark} />
					<ButtonSpace align="center">
						<Link href={"/"}><Button>나가기</Button></Link>
						<StyledButtonDiv>
							<Button type="primary" onClick={isDoneWritePost}>출간하기</Button>
						</StyledButtonDiv>
					</ButtonSpace>
				</WriteDiv>}
		</Layout>
	);
}
