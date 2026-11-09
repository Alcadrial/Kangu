import { Routes } from "@angular/router"
import { USER_PATHS } from "../../constants/path.constants"
import { Chat } from "./chat"


export const routes: Routes = [
    {
        path: "",
        component: Chat
    },
    {
        path: USER_PATHS.settings,
        component: Chat
    },
    {
        path: ":user",
        component: Chat
    },
]