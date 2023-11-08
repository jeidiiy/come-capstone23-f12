
import React from "react";

import {Button, Layout, Space} from "antd";

import {useRouter} from "next/router";

const Write = () => {
	const router = useRouter();

	return (
		<Layout style={{height: "100vh", display: "flex", justifyContent: "center", alignItems: "center", flexDirection: "column"}}>
			<Space>
				<Button size="large" onClick={() => router.replace("/write/self")}>직접 글 작성하기</Button>
				<Button size="large" onClick={() => router.replace("/write/notion")}>노션 글 불러오기</Button>
			</Space>
		</Layout>
	);
};

export default Write;
