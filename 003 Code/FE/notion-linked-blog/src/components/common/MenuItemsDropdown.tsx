import Link from "next/link";
import {Dropdown, Space, Typography} from "antd";
import {DownOutlined, UserOutlined} from "@ant-design/icons";
import styled from "styled-components";
import {useSelector} from "react-redux";
import {RootState} from "@/redux/store";

import data from "./menuItemData.json";

const {Text} = Typography;

const StyledSpace = styled(Space)`
  cursor: pointer;
`;

interface IStyledUserOutlined {
	isDark: boolean;
}

const StyledUserOutlined = styled(UserOutlined) <IStyledUserOutlined>`
  cursor: pointer;
	color: ${props => (props.isDark ? "#fff" : "#000")};
`;

interface IStyledDownOutlined {
	isDark: boolean;
}

const StyledDownOutlined = styled(DownOutlined) <IStyledDownOutlined>`
	cursor: pointer;
	color: ${props => (props.isDark ? "#fff" : "#000")};
`;

export default function MenuItemsDropdown({isDark}) {
	const username = useSelector<RootState, string>(state => state.user.user.username);

	const items = Object.entries(data).map(([text, href]) => ({
		label: <Link href={href === "/myblog" ? `/ ${username} ` : `${href} `}><Text>{text}</Text></Link>,
		key: href,
	}));

	return (
		<Dropdown menu={{items}} trigger={["click"]}>
			<StyledSpace>
				<StyledUserOutlined isDark={isDark} />
				<StyledDownOutlined isDark={isDark} />
			</StyledSpace>
		</Dropdown >
	);
}
