import {useState, useEffect} from "react";
import {useRouter} from "next/router";
import {getPostByIdAPI} from "@/apis/post";
import AppLayout from "@/components/common/AppLayout";
import PostViewer from "@/components/post/PostViewer";
import convertKRTimeStyle from "@/utils/time";

export default function Page({isDark}) {
	const router = useRouter();
	const [post, setPost] = useState({});

	useEffect(() => {
		if (router.isReady) {
			(async function() {
				const params = router.query;

				const receivedPost = await getPostByIdAPI(params.id as string);

				receivedPost.createdAt = convertKRTimeStyle(receivedPost.createdAt);

				setPost(receivedPost);
			})();
		}
	}, [router.isReady]);

	return (
		<AppLayout>
			<PostViewer post={post} isDark={isDark} />
		</AppLayout>
	);
}
